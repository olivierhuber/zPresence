package com.zenika.zpresence.prevayler.model;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import org.apache.commons.codec.binary.Base64;
import org.vertx.java.core.json.JsonArray;
import org.vertx.java.core.json.JsonObject;

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
            JsonArray results = new JsonArray();
            events.stream().forEach(event -> results.addObject(new JsonObject().putString("id", Base64.encodeBase64URLSafeString(event.getBytes())).putString("name", event)));
            return results;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
