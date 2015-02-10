# zPresence
Ou comment gérer avec Zenitude les présences à vos évènements

Coucou, on a eu un besoin sur Paris et je pense que ça peut intéresser certain alors je partage ...

Voila, on avait besoin pour un évènement (comme une NightClazz au hasard) d'accueillir avec son mobile ou tablette (avec perte de connexion internet possible) chaque personne qui arrive et cliquer sur "présent" devant leur nom et ensuite revenir à son poste et avoir un export Excel avec tout les présents ;)

Je vous laisse tester le bousin en attaché de ce mail : ATTENTION Java 8 minimum

Alors vous double-cliquez dessus (pour les windows) ou vous lancer un java -jar

Pour les plus curieux, il s'agit d'un fat jar avec du Vert.x et du NoSQL Prevayler dedans ;)

Vous vous connecter une première fois avec votre mobile à l'adresse du serveur lancé sur votre PC et ensuite vous pouvez vous éloigner et même perdre la connexion et cela continue de fonctionner et dès que votre mobile se retrouve à une distance ou il retrouve le réseaux tout se resynchronise ;)
C'est du WebSocket avec du SockJS branché sur le bus Vert.x ... trop d'la balle ;)
