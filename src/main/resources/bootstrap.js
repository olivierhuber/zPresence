//var vertx = require('vertx');
var console = require('vertx/console');
var container = require('vertx/container');

var config = container.config;

if (!config.prevayler) {
    console.warn("No config provided! Fallback on default conf");
    config = {
        "prevayler": { "prevalenceBase": ".zpresence" },
        "webServer": {
            "port": 8080,
            "route_matcher": true,
            "bridge": true,
            "inbound_permitted": [
                {}
            ],
            "outbound_permitted": [
                {}
            ]

        }
    }
}

container.deployWorkerVerticle('com.zenika.zpresence.prevayler.PrevaylerVerticle', config.prevayler, 1, function (error) {
    if (error) {
        console.log("PrevaylerVerticle error...");
        console.log(error);
    } else {
        console.log("PrevaylerVerticle started!");

        container.deployVerticle('com.zenika.zpresence.RestVerticle', config.webServer, 1, function (error) {
            if (error) {
                console.log("WebServer error...");
                console.log(error);
            } else {
                console.log("WebServer started! Listening on port " + config.webServer.port);
                console.log("Open default browser!");
                java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://127.0.0.1:" + config.webServer.port));
            }
        });
    }
});
