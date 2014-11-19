//var vertx = require('vertx');
var console = require('vertx/console');
var container = require('vertx/container');

var config = container.config;

container.deployWorkerVerticle('com.zenika.zpresence.prevayler.PrevaylerVerticle', config.prevayler, 1, function(error){
    if (error) {
        console.log("PrevaylerVerticle error...");
        console.log(error);
    } else {
        console.log("PrevaylerVerticle started!");

        container.deployVerticle('com.zenika.zpresence.RestVerticle', config.webServer, 1, function(error){
            if (error) {
                console.log("WebServer error...");
                console.log(error);
            } else {
                console.log("WebServer started! Listening on port " + config.webServer.port);
            }
        });
    }
});
