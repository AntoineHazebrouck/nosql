# TP 5.4

## MongoDB - Scaling horizontal

## R5.A.10 - NoSQL

## BUT3 - Info
## Philippe Mathieu
## 2023–2024

#### Objectifs

Savoir déployer un cluster Mongo. Comprendre la gestion de la réplication, de la cohérence et de la tolérance aux pannes.

## Exercice 1 : La réplication : une architecture à 3 machines

La réplication permet d’assurer la haute disponibilité. Mongo utilise pour cela la notion de replica sets qui possèdent chacun un noeud principal servant d’interlocuteur.
Idéalement chaque serveur est sur une machine différente. Avec Docker il faut créer plusieurs services, nous utiliserons
pour cela une stack docker-compose.

#### Q1. Téléchargez le fichier tp54_exo1.zip qui contient un répertoire avec un fichier docker-compose.yaml permettant de créer un replica set de 3 machines

#### Q2. Regardez attentivement ce fichier, notamment les noms et les commandes

#### Q3. Lancez cette configuration avec docker compose up -d

A ce stade vous êtes à la tête de 3 machines nommées node1, node2, node3 dans un replica set nommé shard1. Visualisez les en affichant les processus.

#### Q4. Affichez les options du client mongosh avec l’option --help. Vérifiez notamment la syntaxe pour changer le numéro de port ou pour exécuter une commande online (très pratique pour la suite).

#### Q5. Configurez le replica set sur node2 (Attention : les noeuds servent sur le port 27018)

```shell
docker exec -it node2  mongosh --port 27018
```

```javascript
rs.initiate({ _id : "shard1",
	members:[
		{_id:0 , host:"node1:27018"},
		{_id:1 , host:"node2:27018"},
		{_id:2 , host:"node3:27018"}
	]
})
```

#### Q6. Vérifiez les prompt des 3 noeuds en se connectant sur chacun. Lequel est Primary?

```shell
docker exec -it node1  mongosh --port 27018
docker exec -it node2  mongosh --port 27018
docker exec -it node3  mongosh --port 27018
```

#### Q7. Lancez sur n’importe quel noeud la commande rs.status(). Elle vous donne toutes les informations sur ce replica set

#### Q8. rs.status() est assez verbeux. Saurez vous écrire la requête qui affiche uniquement les valeurs name et stateStr de l’Array members

```javascript
rs.status().members.map(member => {
	return { name: member.name, stateStr: member.stateStr }
})
```

#### Q9. Sur le noeud Primary, dans la base test créez une collection macoll et mettez une donnée x à 1 dedans

```javascript
db.createCollection('macoll')

db.macoll.insert(
   { x: 1}
)
```

#### Q10. Connectez vous à un noeud secondaire et affichez la collection macoll

c'est impossible, il faut etre sur une machine primaire

```javascript
db.macoll.find()
```

#### Q11. Tuez brutalement (docker kill) un noeud secondaire. Vérifiez le statut du replica set

```shell
docker kill node3
```
```shell
docker exec -it node2 mongosh --port 27018
```

```javascript
rs.status().members.map(member => {
	return { name: member.name, stateStr: member.stateStr }
})
```

#### Q12. Ajoutez une donnée y:2 à la collection.

```shell
docker exec -it node2 mongosh --port 27018
```

```javascript
db.macoll.insert(
   { y: 1}
)
```

#### Q13. Redémarrez le noeud précédemment tué. Vérifiez à nouveau le statut du replica set et vérifiez que vous savez relire la collection

```shell
docker start node3
```

```shell
docker exec -it node2 mongosh --port 27018
```

```javascript
rs.status().members.map(member => {
	return { name: member.name, stateStr: member.stateStr }
})
db.macoll.find()
```

#### Q14. Faites un export de la collection macoll du noeud node3. Regardez les données affichées ! Vous constatez que y est bien dans la collection du node3, alors que node3 était mort quand on a ajouté y!

```shell
docker exec -it node3 mongoexport --port 27018 --collection=macoll
```

#### Q15. Tuez brutalement (docker kill) le noeud primaire cette fois. Au bout de 10 secondes, Vérifiez le statut du replica set

```shell
docker kill node2
```

```shell
docker exec -it node3 mongosh --port 27018
```

```javascript
rs.status().members.map(member => {
	return { name: member.name, stateStr: member.stateStr }
})
```

#### Q16. Vérifiez que vous savez relire la collection

```javascript
db.macoll.find()
```

#### Q17. Redémarrer le noeud précédemment tué. Qu’est-ce que ça change ?

```shell
docker start node2
```

```shell
docker exec -it node2 mongosh --port 27018
```

```javascript
rs.status().members.map(member => {
	return { name: member.name, stateStr: member.stateStr }
})
```

**Une fois l’exercice terminé, arrêtez la stack docker avec docker compose down -v
Vérifiez que les commandes docker ps --all et docker volume ls ne retournent plus rien**

## Exercice 2 : Sharding : une architecture à 7 machines

Précédemment il n’y avait qu’un seul shard, et donc pas de fragmentation. Dans cet exercice nous allons créer 2 replica sets de données afin de permettre le sharding.

#### Q1. Téléchargez le fichier tp54_exo2.zip qui contient un répertoire avec un fichier docker-compose.yaml permettant de créer 2 replica set de 2 noeud, et un replicat set de configuration

#### Q2. Regardez attentivement ce fichier, notamment les noms et les commandes

#### Q3. Lancez cette configuration avec docker compose up -d

A ce stade vous avez donc 7 machines nommées node1 node2 dans shard1, node3 et node4 dans shard2, ainsi que config1 et config2 dans configShard et le routeur mongos.

#### Q4. Configurez le replica set du noeud de configuration

```shell
docker exec -it config1  mongosh --port 27019
```

```javascript
rs.initiate({ _id : "configShard",
	members:[
		{_id:0 , host:"config1:27019"},
		{_id:1 , host:"config2:27019"}
	]
})
```

#### Q5. Configurez les 2 replica set de données

```shell
docker exec -it node1 mongosh --port 27018
```

```javascript
rs.initiate({ _id : "shard1",
	members:[
		{_id:0 , host:"node1:27018"},
		{_id:1 , host:"node2:27018"}
	]
})
```

```shell
docker exec -it node3 mongosh --port 27018
```

```javascript
rs.initiate({ _id : "shard2",
	members:[
		{_id:0 , host:"node3:27018"},
		{_id:1 , host:"node4:27018"}
	]
})
```

#### Q6. Connectez vous au routeur mongos et ajoutez les 2 shards de données

```shell
docker exec -it mongos mongosh --port 27017
```

```javascript
sh.addShard('shard1/node1:27018')
sh.addShard('shard2/node3:27018')
```

#### Q7. Vérifiez l’état du sharding via sh.status() dans la rubrique shards. Vous devriez voir apparaître vos shards et les noeuds les constituant.

## Exercice 3 : Distribuer des données

#### Q1. Créer une collection users en prenant soin de définir une clé de sharding basée sur le username.

```shell
docker exec -it mongos mongosh --port 27017
```

```javascript
sh.shardCollection('test.users', {username : 1})
```

#### Q2. MongoDB répartit des “chunks”. Un chunk fait par défaut 128Mo, mais il est possible de définir la taille entre 1 et 1024 Mo. Passez la taille des chunks à 1Mo:

```javascript
use config

db.settings.updateOne(
	{ _id: "chunksize" },
	{ 
		$set: 
		{ 
			_id: "chunksize", 
			value: 1 
		} 
	},
	{ upsert: true }
)

use test
```

#### Q3. Ajoutez à votre collection quelques milliers de documents username,age,groupe,created en utilisant une boucle Javascript sur insertOne (voir slide 20 de transp_cours52.pdf).

```javascript
for (let i = 0; i < 10000; i++) {
	db.users.insertOne({
		username: 'username' + i,
		age: 'age' + i,
		groupe: 'groupe' + i
	})
}
```

#### Q4. Vérifiez la répartition des données sur les shards avec db.coll.getShardDistribution(). Cette méthode vous indique notamment le nombre total de chunks, la taille moyenne des chunks, le nombre de documents total, et le détail pour chaque shard.

```javascript
db.users.getShardDistribution()
```

#### Q5. Connectez vous sur node3 pour vérifier le nombre de données gérées par shard2 (via un countDocuments() par exemple), idem pour note1 avec shard1

```shell
docker exec -it node3 mongosh --port 27018
```

```javascript
db.users.countDocuments()
```

```shell
docker exec -it node1 mongosh --port 27018
```

```javascript
db.users.countDocuments()
```

#### Q6. Relancez sur mongos la création de 10.000 documents en ré-exécutant le même programme, et cela tant que la répartition ne se fait pas. Vérifiez à chaque étape le volume des shards.

```shell
docker exec -it mongos mongosh --port 27017
```

```javascript
for (let i = 0; i < 10000; i++) {
	db.users.insertOne({
		username: 'username' + i,
		age: 'age' + i,
		groupe: 'groupe' + i
	})
}
```

```javascript
db.users.getShardDistribution()
```


Toute requête sur un Primary ne concerne que les données du shard correspondant. Toute requête sur le router
concerne tous les shards.
