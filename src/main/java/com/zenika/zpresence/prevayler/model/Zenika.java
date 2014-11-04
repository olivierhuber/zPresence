package com.zenika.zpresence.prevayler.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.vertx.java.core.json.JsonArray;

import java.io.Serializable;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

public class Zenika implements Serializable {
    public static final long serialVersionUID = 1L;
    public static final transient ObjectMapper jsonMapper = new ObjectMapper();

    static {
        jsonMapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
        jsonMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
    }

    public final Map<String, Collection<Person>> events = new HashMap<>();

    public static JsonArray toJson(Collection<String> events) {
        try {
            return new JsonArray(Zenika.jsonMapper.writeValueAsString(events));
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
