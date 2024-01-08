# TP 5.5 REDIS - CRUD
## R5.A.10 - NoSQL
## BUT3 - Info
## Philippe Mathieu
## 2023–2024

#### Objectifs

Redis est une base de données NoSQL open-source de type clé-valeur à haute disponibilité. Redis peut gérer jusqu’à 16 databases avec 2^32 clés dans chacune. Ce TP a pour objectif de faire un tour rapide de ses différentes fonctionnalités. Manuel Redis

## Exercice 1 : Installation du serveur Redis

Il existe différentes manières d’installer Redis. Via votre gestionnaire de paquetage (apt-get install) , via le tarball fourni sur le site officiel https://redis.io/download/ ou via un service docker (voir Annexe). Pour des raisons de simplicité nous utiliserons docker.

#### Q1. créez une fois pour toutes le service docker avec la dernière version de Redis

```shell
docker run --name monredis -v mes_data_redis:/data -p 6379:6379 -d redis:latest
```
#### Q2. Connectez vous à ce service via le client standard redis-cli

```shell
docker exec -it monredis redis-cli

docker exec -it monredis bash
```

## Exercice 2 : Bien démarrer

#### Q1. Tapez la commande PING

Si tout s’est bien passé le serveur vous renvoie un PONG

#### Q2. Tapez la commande DBSIZE

Cette commande fournit le nombre de clés de la base courante. A priori 0

#### Q3. Tapez SELECT 5

Cette commande change de base et passe à la base 5. Cela se voit dans le prompt. Si rien n’est indiqué cela correspond
à la base 0.

#### Q4. Tapez la commande CONFIG GET *

Cette commande permet de voir l’ensemble des paramètres du serveur actuel. Redis fournit toujours ses réponses avec les valeurs écrites sous les clés. L’ensemble des lignes de sorties est numéroté. Vous devriez avoir 390 lignes, ce qui
correspond à 195 clés.

#### Q5. On peut bien sûr rechercher une clé spécifique. Tapez la commande CONFIG GET dbfilename

Vous obtenez alors une clé-valeur spécifique.

#### Q6. Un joker peut être utilisé dans les recherches. Tapez la commande CONFIG GET active*

Des jokers peuvent être utilisés pour référencer des groupes de clés. Il y en a ici 9 (active, tls, repl,...).

#### Q7. Tapez la commande QUIT

Vous venez de quitter votre première session avec REDIS.

Tour d’horizon des différents types de données
Pour les exercices suivants, reconnectez vous à la base 0

## Exercice 3 : Les chaines

Les chaines de caractères sont les données de bases. Il n’y a pas d’entier en REDIS, tout est représenté sous forme
de chaines. Attention : les données sont case-sensitive.

#### Q1. Affectez la clé nom à la valeur paul

```shell
SET nom paul
```

#### Q2. Affichez la valeur de cette clé

```shell
GET nom
```

#### Q3. Affectez la clé compteur à la valeur 2

```shell
SET compteur 2
```

#### Q4. incrémentez compteur de 1

```shell
INCR compteur
```

#### Q5. Affichez la valeur de cette clé

```shell
GET compteur
```

#### Q6. incrémentez compteur de 10

```shell
INCRBY compteur 10
```

#### Q7. Affichez la valeur de cette clé

```shell
GET compteur
```

#### Q8. Affichez toutes les clés

```shell
KEYS *
```


## Exercice 4 : Le TTL

Il est possible de définir le temps d’expiration d’une clé à l’aide de la commande EXPIRE ou de l’argument EX. −1 indique qu’il n’y a pas d’expiration. Quand la valeur arrive à −2 la clé n’est plus accessible.

#### Q1. Affectez la clé compteur à la valeur 20

```shell
SET compteur 20
```

#### Q2. Donnez à cette clé une validité de 10 sec

```shell
EXPIRE compteur 10
```

#### Q3. Affichez plusieurs fois sa validité jusqu’à arriver à -2

```shell
SET compteur 2
EXPIRE compteur 10
TTL compteur (plusieurs fois jusqu'à -2)
GET compteur
SET compteur 2 ex 10
TTL compteur (plusieurs fois jusqu'à -2)
```

#### Q4. Tentez de lire la valeur de compteur

```shell
GET compteur
```

#### Q5. Recommencez la même démarche mais en indiquant une date de validité à l’affectation.

```shell
SET compteur4 10 EXAT 1704712349
```

#### Q6. Affichez les clés de la base

```shell
KEYS *
```

## Exercice 5 : Les listes

Les listes permettent de sauvegarder une liste d’éléments (l’ordre d’empilement est toujours conservé)

#### Q1. Créez une liste chats avec plusieurs races de chats (birman mainecoon ragdol somali abyssin bengal ...)

```shell
LPUSH chats birman mainecoon ragdol somali abyssin bengal
```

#### Q2. Affichez la longueur de la liste chats

```shell
LLEN chats
```

#### Q3. Affichez les 3 premiers de la liste

```shell
LRANGE chats 0 3
```

#### Q4. Rajoutez 3 races en fin de liste (burmese siamois persan)

```shell
RPUSH burmese siamois persan
```

#### Q5. Affichez la nouvelle longueur

```shell
LLEN chats
```

#### Q6. Affichez toute la liste

```shell
LRANGE chats 0 -1
```

## Exercice 6 : Les sets

Les sets permettent de sauvegarder des informations de manière non ordonnées mais avec unicité. Contrairement aux liste un set ne peut contenir plusieurs fois le même élément.

#### Q1. Créez un set mult3 contenant tous les multiples de 3 jusque 20

```shell
SADD mult3 3 6 9 12 15 18
```

#### Q2. Créez un set mult5 contenant deux fois tous les multiples de 5 jusque 20

```shell
SADD mult5 5 5 15 15 20 20
```

#### Q3. Affichez toutes les valeurs de mult5

```shell
SMEMBERS mult5
```

#### Q4. Combien de valeurs avez vous rentré ? combien y-en a t-il ? Pourquoi ?

6, 3, car set = pas de doublons

#### Q5. Affichez l’intersection de ces 2 sets

```shell
SINTER mult3 mult5
```

#### Q6. Rangez dans res la différence mult3 - mult5

```shell
SDIFFSTORE res mult3 mult5
```

## Exercice 7 : Les sets avec score

Il est aussi possible de créer des sets ordonnés en introduisant une notion de score.

#### Q1. Ajoutez dans une clé classement le nom paul avec un score de 10

```shell
ZADD classement 10 paul
```

#### Q2. Ajoutez dans une clé classement le nom marie avec un score de 15

```shell
ZADD classement 15 marie
```

#### Q3. Ajoutez dans une clé classement le nom louis avec un score de 3

```shell
ZADD classement 3 louis
```

#### Q4. Affichez tout le contenu de classement

```shell
ZRANGE classement 0 -1
```
#### Q5. Affichez le rang de paul

```shell
ZRANK classement paul
```

#### Q6. Effacez louis

```shell
ZREM classement louis
```

#### Q7. Affichez à nouveau le rang de paul

```shell
ZRANK classement paul
```

## Exercice 8 : Les hashes

Les hashs permettent de sauvegarder des informations plus complexes qui ont des sous propriétés, des sortes d’objets.

#### Q1. Créez un hashes user:1 avec la propriété nom à paul

```shell
HSET user:1 nom paul
```

#### Q2. Ajoutez la propriété age à 23

```shell
HSET user:1 age 23
```

#### Q3. Ajoutez la propriété email à paul@gmail.com

```shell
HSET user:1 email paul@gmail.com
```

#### Q4. Affichez tous les éléments

```shell
HGETALL user:1
```

#### Q5. Créez en une seule instruction un hashes user:2 avec les propriétés nom,prenom age à virginie, 25, virginie@gmail.com

```shell
HSET user:2 nom virginie age 25 email virginie@gmail.com
```
#### Q6. Affichez toutes les valeurs de user:2

```shell
HGETALL user:2
```

#### Q7. Affichez uniquement la valeur de nom de user:2

```shell
HGET user:2 nom
```

Utilisation standard de Redis

## Exercice 9 : Pub/Sub

Ouvrir 3 redis-cli. Le premier servira à diffuser les messages, et chacun des deux autres sera abonné à un canal spécifique.

#### Q1. Pour s’y retrouver facilement, Redis permet d’affecter un nom à chaque client avec client setname <lenom>.

Affectez Diffuseur, Paul et Virginie à chacun de ces clients.

```shell
CLIENT SETNAME Diffuseur
CLIENT SETNAME Paul
CLIENT SETNAME Virginie
```

#### Q2. La commande client list permet d’avoir les informations sur l’ensemble des clients connectés. Quel est l’age de chaque client ? Quelle est leur durée d’inactivité ?

```shell
CLIENT LIST
```

#### Q3. Dans le client Paul tapez : SUBSCRIBE sports

```shell
SUBSCRIBE sports
```

#### Q4. Dans le client virginie tapez : SUBSCRIBE news

Vous constatez que les clients sont maintenant bloqués et en attente.

```shell
SUBSCRIBE news
```

#### Q5. Dans le client Diffuseur tapez :

```shell
PUBLISH sports "PSG va à nouveau rencontrer Marseille"
PUBLISH news "Les élections présidentielles auront lieu le 10 avril"
```

Le mode SUBSCRIBE peut-être arrêté avec un Ctrl+C dans la console du client.

## Exercice 10 : Utiliser Redis dans un langage de programmation

L’une des utilisations fréquentes de Redis, consiste à l’utiliser comme système de cache : plutôt que de refaire des calculs, on les range dans REDIS et à chaque recherche, on vérifie si ça n’a pas déjà été calculé.

#### Q1. Ecrire un programme java TP55RestSansCache.java avec comme paramètre un numéro de user, et qui exécute une requête REST de type GET sur le site jsonplaceholder et affiche les données du user correspondant.

Un programme équivalent à curl -X GET https://jsonplaceholder.typicode.com/users/8
....

```java
URL url = new URL("https://jsonplaceholder.typicode.com/users/5");
// + Integer.parseInt(args[0]));
HttpURLConnection con = (HttpURLConnection) url.openConnection();
con.setRequestMethod("GET");
int responseCode = con.getResponseCode();
System.out.println("CODE: "+responseCode); // A priori 200
.....
// lire les lignes de la réponse en parcourant con.getInputStream()
....
con.disconnect();
```

Si vous lancez plusieurs fois ce programme, il se reconnecte à chaque fois à jsonplaceholder, et ré-exécute la requête. Il prendra donc à chaque exécution un temps constant.

#### Q2. Modifiez ce programme pour qu’il affiche aussi son temps d’exécution en millisecondes
#### Q3. Téléchargez la librairie Jedis qui permet d’interagir avec Redis en Java
#### Q4. Ecrire maintenant un programme java TP55RestAvecCache.java qui reprend le précédent mais qui, avant d’exécuter la requête, regarde dans REDIS si la réponse y est déjà. Dans le cas contraire, la requête est alors effectuée et la réponse est rangée dans REDIS (une clé par utilisateur).

Si vous lancez plusieurs fois ce programme sur le même utilisateur, il se connecte plus qu’une seule fois (la première).
Par ailleurs les données sont maintenant persistantes.
#### Q5. Comparez les temps d’exécution.
#### Q6. Vérifiez dans la base REDIS que les données récupérées sont bien persistantes.