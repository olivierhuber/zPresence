package com.zenika.zpresence.prevayler.command;

import com.zenika.zpresence.prevayler.model.Zenika;
import org.prevayler.Transaction;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Date;

public class EditPresence implements Transaction<Zenika> {
    private final String event;
    private final JsonArray people;

    public EditPresence(String event, JsonArray people) {
        this.event = event;
        this.people = people;
    }

    @Override
    public void executeOn(Zenika zenika, Date executionTime) {
        people.forEach(updatePerson -> zenika.events.get(event).forEach(person -> {
            JsonObject updatePersonAsJson = (JsonObject) updatePerson;
            if (person.email.equals(updatePersonAsJson.getString("email"))) {
                person.presence = updatePersonAsJson.getBoolean("presence");
            }
        }));
    }
}
