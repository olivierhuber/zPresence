//var vertx = require('vertx');
var console = require('vertx/console');
var container = require('vertx/container');

console.log('Config provided ' + JSON.stringify(container.config));

var config = {
    "prevayler": { "prevalenceBase": container.config.data || java.lang.System.getProperty("user.home") + "/.zpresence" },
    "webServer": {
        "port": container.config.http || 8080,
        "route_matcher": true,
        "bridge": true,
        "inbound_permitted": [
            {}
        ],
        "outbound_permitted": [
            {}
        ]
    }
};

container.deployWorkerVerticle('com.zenika.zpresence.prevayler.PrevaylerVerticle', config.prevayler, 1, function (prevaylerError, prevaylerDeployID) {
    if (prevaylerError) {
        console.log("PrevaylerVerticle error...");
        console.log(prevaylerError);
    } else {
        console.log("PrevaylerVerticle started! Data directory is " + config.prevayler.prevalenceBase);

        container.deployVerticle('com.zenika.zpresence.RestVerticle', config.webServer, 1, function (webError) {
            if (webError) {
                console.log("WebServer error...");
                console.log(webError);
                console.log("Shutting down PrevaylerVerticle...");
                container.undeployVerticle(prevaylerDeployID);
                if (webError.message.indexOf("Address already in use") != -1) {
                    console.log("Open default browser on already running instance of ZPresence listening on port " + config.webServer.port);
                    java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://127.0.0.1:" + config.webServer.port));
                }
                container.exit();
            } else {
                console.log("WebServer started! Listening on port " + config.webServer.port);
                console.log("Open default browser!");
                java.awt.Desktop.getDesktop().browse(java.net.URI.create("http://127.0.0.1:" + config.webServer.port));
            }
        });
    }
});
