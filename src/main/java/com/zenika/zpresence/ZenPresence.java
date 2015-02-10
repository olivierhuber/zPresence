package com.zenika.zpresence;

import org.vertx.java.busmods.BusModBase;
import org.vertx.java.core.json.JsonObject;

public abstract class ZenPresence extends BusModBase {
    long starttime;

    @Override
    public void start() {
        super.start();

        starttime = System.currentTimeMillis();

        vertx.setPeriodic(5000, event -> {
            JsonObject health = new JsonObject();
            health.putString("verticle", verticleName());
            health.putString("version", "unknown");
            health.putNumber("startup", starttime);
            health.putNumber("heartbeat", System.currentTimeMillis());
            health.putObject("config", config);
            eb.publish("health-check", health);
        });
    }

    abstract public String verticleName();

    protected void info(String message) {
        publishLog("info", message);
    }

    protected void warn(String message) {
        publishLog("warn", message);
    }

    protected void error(String message) {
        publishLog("error", message);
    }

    private void publishLog(String level, String message) {
        String m = verticleName() + ":" + message;

        switch (level) {
            case "info": logger.info(m); break;
            case "warn": logger.warn(m); break;
            case "error": logger.error(m); break;
        }

        JsonObject log = new JsonObject();
        log.putString("level", level);
        log.putNumber("timestamp", System.currentTimeMillis());
        log.putString("message", m);
        eb.publish("log", log);
    }
}
