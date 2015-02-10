package com.zenika.zpresence.prevayler.command;

import com.zenika.zpresence.prevayler.model.Person;
import com.zenika.zpresence.prevayler.model.Zenika;
import org.prevayler.Query;

import java.util.Collection;
import java.util.Date;

public class GetPeople implements Query<Zenika, Collection<Person>> {
    private final String event;

    public GetPeople(String event) {
        this.event = event;
    }

    @Override
    public Collection<Person> query(Zenika zenika, Date date) throws Exception {
        return zenika.events.get(event);
    }
}
