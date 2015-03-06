package com.zenika.zpresence.prevayler;

import com.hazelcast.util.StringUtil;
import com.zenika.zpresence.ZenPresence;
import com.zenika.zpresence.prevayler.command.*;
import com.zenika.zpresence.prevayler.migration.JsonSnapshotMigration;
import com.zenika.zpresence.prevayler.model.Person;
import com.zenika.zpresence.prevayler.model.Zenika;
import org.prevayler.Prevayler;
import org.prevayler.PrevaylerFactory;
import org.prevayler.implementation.PrevaylerDirectory;
import org.vertx.java.core.eventbus.Message;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Set;

public class PrevaylerVerticle extends ZenPresence {
    String prevalenceBase;
    Prevayler<Zenika> prevayler;

    @Override
    public String verticleName() {
        return getClass().getSimpleName();
    }

    @Override
    public void stop() {
        try {
            snapshot();
            prevayler.close();
        } catch (Exception e) {
            throw new RuntimeException(e);
        } finally {
            super.stop();
        }
    }

    String snapshot() throws Exception {
        String name = prevayler.takeSnapshot().getCanonicalPath();
        System.out.println("Snapshot! " + name);
        cleanupUnusedFiles();
        return name;
    }

    void cleanupUnusedFiles() throws IOException {
        List<File> prevalenceFiles = Arrays.asList(new File(prevalenceBase).listFiles());
        Set<File> necessaryFiles = new PrevaylerDirectory(prevalenceBase).necessaryFiles();

        prevalenceFiles.stream()
                .filter(file -> !necessaryFiles.contains(file))
                .filter(file -> file.getName().startsWith("0000000000000"))
                .filter(file -> file.getName().endsWith("journal") || file.getName().endsWith("jsonSnapshot"))
                .forEach(File::delete);
    }

    @Override
    public void start() {
        super.start();

        prevalenceBase = config.getString("prevalenceBase");
        if (!vertx.fileSystem().existsSync(prevalenceBase)) {
            vertx.fileSystem().mkdirSync(prevalenceBase, true);
        }
        try {
            PrevaylerFactory<Zenika> factory = new PrevaylerFactory<>();
            factory.configurePrevalentSystem(new Zenika());
            factory.configurePrevalenceDirectory(prevalenceBase);
            factory.configureSnapshotSerializer("jsonSnapshot", new JsonSnapshotMigration(Zenika.jsonMapper));
            prevayler = factory.create();
            snapshot();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

        eb.registerHandler("prevayler-store", (Message<JsonObject> message) -> {
            switch (message.body().getString("action")) {
                case "add-event": {
                    addEvent(message);
                    break;
                }
                case "delete-event": {
                    deleteEvent(message);
                    break;
                }
                case "get-events": {
                    getEvents(message);
                    break;
                }
                case "get-people": {
                    getPeople(message);
                    break;
                }
                case "edit-presence": {
                    editPresence(message);
                    break;
                }
                case "edit-people": {
                    editPeople(message);
                    break;
                }
                case "snapshot": {
                    try {
                        sendOK(message, new JsonObject().putString("snapshot", snapshot()));
                    } catch (Exception e) {
                        sendError(message, "snapshot", e);
                    }
                    break;
                }
            }
        });
    }

    void getEvents(Message<JsonObject> message) {
        try {
            Collection<String> events = prevayler.execute(new GetEvents());

            sendOK(message, array("events", Zenika.toJson(events)));
        } catch (Exception e) {
            sendError(message, "getEvents", e);
        }
    }

    void getPeople(Message<JsonObject> message) {
        try {
            String event = message.body().getString("event");
            Collection<Person> people = prevayler.execute(new GetPeople(event));
            if (people == null) {
                throw new Exception("Event(" + event + ") does not exist!");
            }

            sendOK(message, array("people", Person.toJson(people)));
        } catch (Exception e) {
            sendError(message, "getPeople", e);
        }
    }

    void addEvent(Message<JsonObject> message) {
        try {
            String event = message.body().getString("event").trim();
            if (StringUtil.isNullOrEmpty(event)) {
                throw new Exception("Event(" + event + ") is Null or Empty!");
            }
            if (!prevayler.execute(new AddEvent(event))) {
                throw new Exception("Event(" + event + ") already exist!");
            }
            snapshot();
        } catch (Exception e) {
            sendError(message, "addEvent", e);
        }
        getEvents(message);
    }

    void deleteEvent(Message<JsonObject> message) {
        try {
            String event = message.body().getString("event");
            if (!prevayler.execute(new DeleteEvent(event))) {
                throw new Exception("Event(" + event + ") does not exist!");
            }
            snapshot();
        } catch (Exception e) {
            sendError(message, "deleteEvent", e);
        }
        getEvents(message);
    }

    void editPresence(Message<JsonObject> message) {
        try {
            String event = message.body().getString("event");
            if (prevayler.execute(new GetPeople(event)) == null) {
                throw new Exception("Event(" + event + ") does not exist!");
            }
            JsonArray people = message.body().getArray("people");
            prevayler.execute(new EditPresence(event, people));

            snapshot();

            sendOK(message, array("people", Person.toJson(prevayler.execute(new GetPeople(event)))));
        } catch (Exception e) {
            sendError(message, "editPresence", e);
        }
    }

    void editPeople(Message<JsonObject> message) {
        try {
            String event = message.body().getString("event");
            if (prevayler.execute(new GetPeople(event)) == null) {
                throw new Exception("Event(" + event + ") does not exist!");
            }
            JsonArray people = message.body().getArray("people");
            prevayler.execute(new EditPeople(event, people));

            snapshot();

            sendOK(message, array("people", Person.toJson(prevayler.execute(new GetPeople(event)))));
        } catch (Exception e) {
            sendError(message, "editPresence", e);
        }
    }

    JsonObject array(String key, JsonArray array) {
        return new JsonObject().putArray(key, array);
    }
}
