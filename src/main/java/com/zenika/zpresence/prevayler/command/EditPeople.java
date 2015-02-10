package com.zenika.zpresence.prevayler.command;

import com.zenika.zpresence.prevayler.model.Person;
import com.zenika.zpresence.prevayler.model.Zenika;
import org.prevayler.Transaction;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.util.Date;

public class EditPeople implements Transaction<Zenika> {
    private final String event;
    private final JsonArray people;

    public EditPeople(String event, JsonArray people) {
        this.event = event;
        this.people = people;
    }

    @Override
    public void executeOn(Zenika zenika, Date executionTime) {
        zenika.events.get(event).clear();
        people.forEach(updatePerson -> zenika.events.get(event).add(Person.fromJson((JsonObject) updatePerson)));
    }
}
