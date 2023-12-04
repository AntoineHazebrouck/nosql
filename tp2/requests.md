# TP 5.1

## MongoDB - CRUD

## R5.A.10 - NoSQL

## BUT3 - Info
## Philippe Mathieu
## 2023–2024

#### Objectifs
Savoir écrire des requêtes d’interrogation avec le pipeline aggregate de MongoDB
L’agrégation est une manière de traiter les documents d’une collection en les faisant passer par différentes étapes.
Ces étapes constituent ce que l’on appelle un pipeline. Les étapes d’un pipeline peuvent filtrer, trier, grouper, remodeler
et modifier les documents qui passent par le pipeline.
L’instruction correspondante s’écrit db.collection.aggregate(pipeline, options)
pipeline étant une liste contenant les différentes étapes à exécuter
```
pipeline = [
{ $match : { ... } },
{ $group : { ... } },
{ $sort : { ... } }
]
```
https://www.mongodb.com/docs/manual/aggregation/
Préambule
#### 1. Créez une instance docker avec la dernière version de Mongo
#### 2. Importez le fichier employes.json dans votre base test au sein du collection employtes (commande mongoimport)
#### 3. Connectez vous à ce service via le client standard mongosh

docker exec -it monmongo mongosh
ou
docker exec -it monmongo bash

#### 4. Listez les collections présentes

## Exercice 1 : Prise en main

### Q1. Listez tous les employés (en utilisant find puis en utilisant aggregate)

```
db.employes.find()

db.employes.aggregate()
```

### Q2. Listez les employés triés par nom décroissant. Quel est le dernier affiché ? (en utilisant find puis en utilisant aggregate)

```javascript
db.employes.aggregate([
	{$sort: {nom:-1}}
])

db.employes.find().sort({nom:-1})
```

Leterre

### Q3. Listez uniquement les noms et prénoms des employés triés par nom décroissant (en utilisant find puis en utilisant aggregate)

```javascript
db.employes.find({}, { nom: 1, prenom: 1}).sort({nom:-1})

db.employes.aggregate([
	{ $project: { nom: 1, prenom: 1 } },
	{ $sort: { nom: -1 } }
])
```

Leterre

### Q4. Listez les employés dont l’ancienneté est supérieure à 20 (en utilisant find puis en utilisant aggregate)

```javascript
db.employes.find({ anciennete: { $gt: 20 } })

db.employes.aggregate([
	{ $project: { nom: 1, anciennete: 1 } },
	{ $match : { anciennete : { $gt: 20 } } }
])
```

### Q5. Listez uniquement le nom et l’ancienneté des employés, pour ceux dont l’ancienneté est supérieure à 10, affichés par ordre d’ancienneté décroissante

```javascript
db.employes.aggregate([
	{ $project: { nom: 1, anciennete: 1 } },
	{ $match : { anciennete : { $gt: 10 } } },
	{ $sort: { nom: -1 } }
])
```

### Q6. Calculez et affichez la moyenne de toutes les anciennetés.

```javascript
db.employes.aggregate([
	{ 
		$group: 
		{
			_id: null,
			avgAnciennete: 
			{ 
				$avg: "$anciennete"
			}
		}
	}
])
```

### Q7. Calculez et affichez la moyenne de l’ancienneté des employés qui ont plus de 20 ans d’ancienneté.

```javascript
db.employes.aggregate([
	{
		$match:
		{
			anciennete: 
			{
				$gt: 20
			}
		}
	},
	{ 
		$group: 
		{
			_id: null,
			avgAnciennete: 
			{ 
				$avg: "$anciennete"
			}
		}
	}
])
```

### Q8. Calculez et affichez la somme de l’ancienneté par prénom d’employé, l’ensemble trié par prénom croissant.

```javascript
db.employes.aggregate([
	{ 
		$group: 
		{
			_id: "$prenom",
			sumAnciennete: 
			{ 
				$sum: "$anciennete"
			}
		}
	},
	{
		$sort:
		{
			sumAnciennete: -1
		}
	}
])
```

### Q9. Calculez et affichez la somme de l’ancienneté par prénom d’employé mais uniquement pour les sommes supérieures à 20 (une sorte de Having)

```javascript
db.employes.aggregate([
	{ 
		$group: 
		{
			_id: "$prenom",
			sumAnciennete: 
			{ 
				$sum: "$anciennete"
			}
		}
	},
	{
		$match:
		{
			sumAnciennete:
			{
				$gt: 20
			}
		}
	}
])
```

## Exercice 2 : Jouer avec les structures : Documents plats

On s’intéresse maintenant au fichier notes1.json. Cette collection contient les notes obtenues par les étudiants
dans différentes matières. Chaque document contient une seule note. Il n’y a aucune structure, c’est ce qu’on appelle
un "fichier plat" (qui pourrait de ce fait être tout aussi bien décrit en CSV).

### Q1. Récupérez ce fichier sur Moodle et importez le dans votre base Mongo dans une collection notes1

```bash
docker exec -it monmongo mongosh
```

### Q2. Affichez le premier document

```javascript
db.notes1.aggregate([
	{
		$limit: 1
	}
])
```

### Q3. Listez tous les documents sans afficher l’id

```javascript
db.notes1.aggregate([
	{
		$project:
		{
			_id: 0
		}
	}
])
```

### Q4. Affichez le nombre de documents

```javascript
db.notes1.aggregate([
	{ 
		$group: 
		{ 
			_id: null, 
			myCount: { $sum: 1 } 
		} 
	}
])
```

### Q5. Affichez les documents de l’étudiant user18 sans afficher l’id

```javascript
db.notes1.aggregate([
	{
		$project:
		{
			_id: 0
		}
	},
	{
		$match:
		{
			nom: "user18"
		}
	}
])
```

### Q6. Affichez la moyenne de chaque étudiant, avec son nom et son groupe. Vérifiez avec user18.

```javascript
db.notes1.aggregate([
	{
		$group:
		{ 
			_id: 
			{ 
				nom: "$nom", 
				groupe: "$groupe" 
			},
			moyenne: 
			{
				$avg: "$valeur"
				
			}
		}
	}
])
```

### Q7. Afficher le nombre de notes de chaque étudiant

```javascript
db.notes1.aggregate([
	{
		$group:
		{ 
			_id: "$nom",
			nbNotes: 
			{
				$sum: 1
			}
		}
	}
])
```

### Q8. Afficher la distribution des nombres de notes (combien d’étudiants ont x notes) 11 étudiants ont 7 notes, 13 en ont 6, 10 en ont 5 etc .... Triez le résulat par nombre de notes croissant.

```javascript
db.notes1.aggregate([
	{
		$group:
		{ 
			_id: 
			{ 
				nom: "$nom", 
				groupe: "$groupe" 
			},
			moyenne: 
			{
				$avg: "$valeur"
				
			}
		}
	},
	{
		$group:
		{
			_id: "$moyenne",
			nbEtu:
			{
				$sum: 1
			}
		}
	}
])
```

### Q9. Affichez les notes minimum de chaque étudiant, avec son nom et son groupe. Vérifiez avec 18.

```javascript
db.notes1.aggregate([
	{
		$group:
		{ 
			_id: "$nom",
			min: 
			{
				$min: "$valeur"
				
			}
		}
	}
])
```

### Q10. Affichez les noms des étudiants qui ont au moins une fois la note minimum de toute la collection. On pourra s’y prendre en 4 étapes :
#### 1. étape1 : on calcule les minimums individuels de chaque étudiant
#### 2. étape2 : sur ce résultat, on calcule le min total tout en construisant le tableau des étudiants qui y sont
#### 3. étape 3 : On déplie tous les noms du tableau avec la note
#### 4. étape 4 : on filtre l’affichage

## Exercice 3 : Jouer avec les structures : Document avec Array

### Q1. Créez à partir de notes1 une collection notes2 de manière à avoir toutes les notes d’un même étudiant dans le même document, sous forme d’un tableau de notes. On fera en sorte d’avoir des documents de la forme :

```javascript
{ 
	nom: 'user13', 
	groupe: 'C', 
	valeurs: [ 11, 10, 8 ] 
}
```

```javascript
db.notes1.aggregate([
	{
		$group:
		{ 
			_id: 
			{ 
				nom: "$nom",  
			},
			nom: 
			{
				$first: "$nom"
			},
			groupe: 
			{
				$first: "$groupe"
			},
			notes: 
			{
				$push: "$valeur"
			}
		}
	},
	{
		$project:
		{
			_id: 0
		}
	},
	{ 
		$out: 
		{ 
			db: "test", coll: "notes2" 
		}
	}
])
```

### Q2. Affichez le premier document

```javascript
db.notes2.aggregate([
	{
		$limit: 1
	}
])
```
### Q3. Affichez tous les documents sans l’id

```javascript
db.notes2.aggregate([
	{
		$project:
		{
			_id: 0
		}
	}
])
```

### Q4. Affichez le nombre de documents

```javascript
db.notes2.aggregate([
	{
		$group:
		{
			_id: null, 
			count: { $sum: 1 } 
		}
	}
])
```

### Q5. Affichez les documents de l’étudiant user18 sans l’id

```javascript
db.notes2.aggregate([
	{
		$match:
		{
			nom: "user18"
		}
	},
	{
		$project:
		{
			_id: 0
		}
	}
])
```

### Q6. Affichez la moyenne de chaque étudiant, avec son nom. Vérifiez avec le 18.

```javascript
db.notes2.aggregate([
	{
		$project:
		{
			_id: 0,
			nom: 1,
			moyenne: { $avg: "$notes" },
		}
	}
])
```

### Q7. Affichez le nombre de notes de chaque étudiant

```javascript
db.notes2.aggregate([
	{
		$project:
		{
			_id: 0,
			nom: 1,
			moyenne: { $size: "$notes" },
		}
	}
])
```

### Q8. Affichez la distribution des nombres de notes (combien d’étudiants ont x notes) 11 en ont 7 , 13 en ont 6, 10 en ont 5 etc ....

```javascript
db.notes2.aggregate([
	{
		$project:
		{
			_id: 0,
			nom: 1,
			nbNotes: { $size: "$notes" }
		}
	},
	{
		$group:
		{
			_id: "$nbNotes",
			nbEtuPourLaNote: 
			{
				$sum: 1
			} 
		}
	}
])
```

### Q9. Affichez les notes minimum de chaque étudiant, avec son nom et son groupe. Vérifiez avec le 18.

```javascript
db.notes2.aggregate([
	{
		$project:
		{
			_id: 0,
			nom: 1,
			noteMin: { $min: "$notes" },
		}
	}
])
```

### Q10. Affichez les noms des étudiants qui ont au moint une fois la note minimum de toute la collection. On pourra s’y prendre en 4 étapes :

<!-- 
```javascript
db.notes2.aggregate([
	{
		$project:
		{
			_id: 0,
			nom: 1,
			noteMin: { $min: "$notes" },
		}
	}
])
``` -->

### Q11. Supprimer le champs valeurs des étudiants qui n’ont pas eu la moyenne de ces valeurs, afin qu’ils passent la 2è session.

## Exercice 4 : Jouer avec les structures : Document avec sous-documents

Dans le fichier notes3.json, comme pour notes2 chaque document correspond à un et un seul étudiant, mais le tableau notes contient cette fois un objet par matière et numéro de contrôle.

### Q1. Récupérez le fichier notes3.json sur Moodle et importez-le dans votre base Mongo dans une collection note3

```shell
docker exec -i monmongo mongoimport -d test -c notes3 < ./tp2/notes3.json
```

### Q2. Afin de bien appréhender la collection ...

#### Q2.1. Affichez le premier document

```javascript
db.notes3.aggregate([
	{
		$limit: 1
	}
])
```

#### Q2.2. Affichez le nombre de documents

```javascript
db.notes3.aggregate([
	{
		$group:
		{
			_id: null,
			count:
			{
				$sum: 1
			}
		}
	}
])
```

### Q3. Calculez la moyenne des notes de bdd

```javascript
db.notes3.aggregate([
	{ 
		$unwind: "$notes"
	},
	{
		$match:
		{
			"notes.mat": "bdd"
		}
	},
	{
		$group:
		{
			_id: null,
			moyenneBdd:
			{
				$avg: "$notes.valeur"
			}
		}
	}
])
```

### Q4. Sachant qu’il y a 7 objets contrôle dans l’array notes et que, quand un étudiant a été absent, l’object contrôle n’y est pas, affichez les noms des étudiants à qui il manque des notes.

```javascript
db.notes3.aggregate([
	{
		$project:
		{
			_id: 1,
			username: 1,
			arraySize: {$size: "$notes"}
     	}
	},
	{
		$match:
		{
			"arraySize": { $lt: 8 }			
		}
	}
])

db.notes3.aggregate([
	{
		$unwind: "$notes",
	},
	{
		$group:
		{
			_id: "$_id",
			nbControles:
			{
				$sum: 1
			}
		}
	},
	{
		$match:
		{
			nbControles: { $lt: 8 }			
		}
	}
])
```

,
	{
		$match:
		{
			"arraySize": { $lt: 7 }			
		}
	}


### Q5. Affichez maintenant les noms des étudiants à qui il manque des notes en BDD
### Q6. Affichez maintenant le nom de l’étudiant, la matière et le numéro du contrôle manquant

A la fin de ce TP détruisez complètement les instances docker que vous avez créés.
Vérifiez que les commandes docker ps --all et docker volume ls ne retournent plus rien