package com.zenika.zpresence.prevayler.migration;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.zenika.zpresence.prevayler.model.Zenika;
import org.prevayler.foundation.serialization.Serializer;
import sun.awt.CharsetString;

import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.Scanner;

import static java.nio.charset.StandardCharsets.UTF_8;

public class JsonSnapshotMigration implements Serializer {
    private ObjectMapper mapper;

    public JsonSnapshotMigration(ObjectMapper mapper) {
        this.mapper = mapper;
    }

    @Override
    public void writeObject(OutputStream stream, Object object) throws Exception {
        mapper.writeValue(stream, object);
    }

    @Override
    public Object readObject(InputStream stream) throws Exception {
        String input;
        try (Scanner s = new Scanner(stream, UTF_8.name())) {
            input = s.useDelimiter("\\A").next();
        }
        try {
            System.out.println("Reading the latest json snapshot...");
            return mapper.readValue(input, Zenika.class);
        } catch (Exception e) {
            System.err.println(e.getMessage());
            System.out.println("Oops! Maybe the system needs to upgrade from an old version...");
            throw new RuntimeException("... but we don't handle that situation right now :( Please fix the JSON!");
//            JsonNode root = mapper.readTree(input);
//            Zenika zenika = new Zenika();
//            return zenika;
        }
    }
}
