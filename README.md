# ZenPresence

ISSUES
===
Seuleument sous windows si on met un "é" dans le titre de l'événement il ne s'affiche pas correctement...

BUILD & RUN
---
    mvn clean package
    cd target
    vertx pulldeps com.zenika~zpresence~1.4.0
    vertx fatjar com.zenika~zpresence~1.4.0

    java -jar zpresence-1.4.0-fat.jar -conf ../src/main/resources/default.conf

    {
        "prevayler" : { "prevalenceBase": "/tmp/.zpresence" },
        "webServer" : {
            "port": 8080,
            "bridge": true,
            "inbound_permitted": [{}],
            "outbound_permitted": [{}]
    
        }
    }

0000000000000000001.jsonSnapshot
---
    {
      "events" : {
        "Titre événement" : [ {
          "email" : "olivierhuber@free.fr",
          "firstname" : "Olivier",
          "lastname" : "Huber"
        } ]
      }
    }

Edit the json data
---
- Stop the server (kill the java process)
- Then you can edit the file .jsonSnapshot
- Then restart the server and check for json parsing error

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
                    email: <string>,
                    firstname: <string>,
                    lastname: <string>,
                    presence: <bool>
            }
        ]
    }

### edit-presence

    {
        action: "edit-presence",
        event: <string>,
        people: [
            {
                    email: <string>,
                    presence: <bool>
            }
        ]
    }     
    
success reply
    
    {
        status: "ok",
        people: [
            {
                    email: <string>,
                    firstname: <string>,
                    lastname: <string>,
                    presence: <bool>
            }
        ]
    }
