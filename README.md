# zPresence
Ou comment gérer avec Zenitude les présences à vos évènements

Coucou, on a eu un besoin sur Paris et je pense que ça peut intéresser certain alors je partage ...

Voila, on avait besoin pour un évènement (comme une NightClazz au hasard) d'accueillir avec son mobile ou tablette (avec perte de connexion internet possible) chaque personne qui arrive et cliquer sur "présent" devant leur nom et ensuite revenir à son poste et avoir un export Excel avec tout les présents ;)

Je vous laisse tester le bousin en attaché de ce mail : ATTENTION Java 8 minimum

Alors vous double-cliquez dessus (pour les windows) ou vous lancer un java -jar

Pour les plus curieux, il s'agit d'un fat jar avec du Vert.x et du NoSQL Prevayler dedans ;)

Vous vous connecter une première fois avec votre mobile à l'adresse du serveur lancé sur votre PC et ensuite vous pouvez vous éloigner et même perdre la connexion et cela continue de fonctionner et dès que votre mobile se retrouve à une distance ou il retrouve le réseaux tout se resynchronise ;)
C'est du WebSocket avec du SockJS branché sur le bus Vert.x ... trop d'la balle ;)

ISSUES
===
- Le fait que l'on export en utilisant le nom de l'event dans l'URL a ses limitation pour nommer les evenements ...

IDEAS
=====
- Base64 du nom de l'évènement dans l'URL serait un plus ;) tant pis pour la lisibilité de l'url

TODO
=====
- Base64 du nom de l'évènement dans l'URL - tant pis pour la lisibilité de l'url
- Ajouter un form avec [prenom], [nom] voir [attributes] et un bouton [ajouter] - tout de même vérifier les effets de bord (import, etc)

BUILD
---
    mvn clean package vertx:fatJar

RUN
---
    java -jar zpresence-<version>-fat.jar

or

    java -jar zpresence-<version>-fat.jar -conf zpresence.json

    { "data" : "/Users/olivier/.zpresence", "http" : 8080 }

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
