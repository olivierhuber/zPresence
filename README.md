# ZenPresence

ISSUES
===
Seuleument sous windows si on met un "é" dans le titre de l'événement il ne s'affiche pas correctement... a vérifier si c'est toujours le cas !

IDEAS
=====
- Base64 du nom de l'évènement dans l'URL serait un plus ;) tant pis pour la lisibilité de l'url


BUILD & RUN
---
    mvn clean package
    cd target
    vertx pulldeps com.zenika~zpresence~2.0.3
    vertx fatjar com.zenika~zpresence~2.0.3

run with

    java -jar zpresence-<version>.jar

or

    java -jar zpresence-<version>.jar -conf ../src/main/resources/default.conf

    {
        "prevayler" : { "prevalenceBase": "/tmp/.zpresence" },
        "webServer" : {
            "port": 8080,
            "route_matcher": true,
            "bridge": true,
            "inbound_permitted": [{}],
            "outbound_permitted": [{}]
    
        }
    }

Edit the json data directly
---
- Stop the server (kill the java process)
- Then you can edit the file .jsonSnapshot
- Then restart the server and check for json parsing error

But the prefered way is to use the admin interface to upload an Excel file

## Publishes

Publishes to the address : `"health-check"`

    {
        verticle: <string>,
        version: <string>,
        startup: <timestamp>,
        heartbeat: <timestamp>
    }

Publishes to the address : `"log"`

    {
        level: <string>,
        timestamp: <timestamp>,
        message: <string>
    }

## Listens

Listens to the address : `"prevayler-store"`

Each error reply will return 

    { status: "error", message: <string> }

### add-event

    {
        action: "add-event",
        event: <string>
    } 
    
success reply
 
    {
        status: "ok",
        events: [
            <string>
        ]
    }

### delete-event

    {
        action: "delete-event",
        event: <string>
    } 
    
success reply
 
    {
        status: "ok",
        events: [
            <string>
        ]
    }

### get-events

    {
        action: "get-events"
    } 
    
success reply
 
    {
        status: "ok",
        events: [
            <string>
        ]
    }

### get-people

    {
        action: "get-people",
        event: <string>
    }
    
success reply

    {
        status: "ok",
        people: [
            {
                    firstname: <string>,
                    lastname: <string>,
                    presence: <bool>,
                    attributes: [<string>]
            }
        ]
    }

### edit-presence

    {
        action: "edit-presence",
        event: <string>,
        people: [
            {
                    firstname: <string>,
                    lastname: <string>,
                    presence: <bool>
            }
        ]
    }     
    
success reply
    
    {
        status: "ok",
        people: [
            {
                    firstname: <string>,
                    lastname: <string>,
                    presence: <bool>,
                    attributes: [<string>]
            }
        ]
    }
    
### edit-people

    {
        action: "edit-people",
        event: <string>,
        people: [
            {
                    firstname: <string>,
                    lastname: <string>,
                    attributes: [<string>]
            }
        ]
    }     
    
success reply
    
    {
        status: "ok",
        people: [
            {
                    firstname: <string>,
                    lastname: <string>,
                    presence: <bool>,
                    attributes: [<string>]
            }
        ]
    }
