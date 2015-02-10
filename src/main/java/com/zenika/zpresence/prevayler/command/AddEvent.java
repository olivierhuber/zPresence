package com.zenika.zpresence.prevayler.command;

import com.zenika.zpresence.prevayler.model.Zenika;
import org.prevayler.TransactionWithQuery;

import java.util.ArrayList;
import java.util.Date;

public class AddEvent implements TransactionWithQuery<Zenika, Boolean> {
    private final String event;

    public AddEvent(String event) {
        this.event = event;
    }

    @Override
    public Boolean executeAndQuery(Zenika zenika, Date executionTime) throws Exception {
        return zenika.events.putIfAbsent(event, new ArrayList<>()) == null;
    }
}
