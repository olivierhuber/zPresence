package com.zenika.zpresence.prevayler.command;

import com.zenika.zpresence.prevayler.model.Zenika;
import org.prevayler.TransactionWithQuery;

import java.util.Date;

public class DeleteEvent implements TransactionWithQuery<Zenika, Boolean> {
    private final String event;

    public DeleteEvent(String event) {
        this.event = event;
    }

    @Override
    public Boolean executeAndQuery(Zenika zenika, Date executionTime) throws Exception {
        return zenika.events.remove(event) != null;
    }
}
