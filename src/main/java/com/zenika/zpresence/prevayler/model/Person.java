package com.zenika.zpresence.prevayler.model;

import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

import java.io.Serializable;
import java.util.Collection;
import java.util.Set;

public class Person implements Serializable {
    public static final long serialVersionUID = 1L;
    public String firstname;
    public String lastname;
    public Boolean presence = false;
    public Set<String> attributes;

    public static Collection<Person> fromJson(JsonArray peopleAsJson) {
        try {
            return Zenika.jsonMapper.readValue(peopleAsJson.encode(), Collection.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static Person fromJson(JsonObject personAsJson) {
        try {
            return Zenika.jsonMapper.readValue(personAsJson.encode(), Person.class);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonObject toJson(Person person) {
        try {
            return new JsonObject(Zenika.jsonMapper.writeValueAsString(person));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public static JsonArray toJson(Collection<Person> people) {
        try {
            return new JsonArray(Zenika.jsonMapper.writeValueAsString(people));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
