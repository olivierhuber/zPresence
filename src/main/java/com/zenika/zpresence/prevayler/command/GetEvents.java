package com.zenika.zpresence.prevayler.command;

import com.zenika.zpresence.prevayler.model.Zenika;
import org.prevayler.Query;

import java.util.Collection;
import java.util.Date;

public class GetEvents implements Query<Zenika, Collection<String>> {
    @Override
    public Collection<String> query(Zenika zenika, Date date) throws Exception {
        return zenika.events.keySet();
    }
}
