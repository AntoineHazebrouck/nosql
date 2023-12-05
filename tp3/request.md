# TP 5.3

## MongoDB - Map-Reduce

## R5.A.10 - NoSQL

## BUT3 - Info
## Philippe Mathieu
## 2023–2024

#### Objectifs

Comprendre le design-pattern Map-Reduce. Savoir l’appliquer avec Mongo.
Préambule Javascript : fonctions et méta-fonctions
Ce premier exercice a comme objectif de comprendre et tester l’utilisation de fonctions et méta-fonctions en javascript.

#### Q1. Ecrire une fonction somme qui renvoie la somme de 2 entiers donnés en paramètre : somme(3,4)

```javascript
somme = function (number1, number2) {
	return number1 + number2
}
```

#### Q2. Ecrire une fonction calcul à 3 paramètres, qui applique la fonction passée en premier paramètre aux deux autres paramètres et renvoie le résultat. Testez cela en passant la fonction somme : calcul(somme,3,4)

```javascript
calcul = function (what, number1, number2) {
	return what(number1, number2)
}
```

### Q3. Appelez calcul en lui passant cette fois une lambda fonction (ou arrow function, ou fonction anonyme) permettant le calcul du produit de 2 paramètres.

```javascript
console.log(calcul(
	(number1, number2) => 
	{ 
		return number1 * number2 
	}, 1, 2))
```


### Préambule Javascript : filter, map, reduce

Javascript possède 3 méta-fonctions qui prennent des fonctions en paramètre : filter, map et reduce.
Pour nos exemples on utilisera un tableau tab contenant les 20 premiers entiers naturels

```javascript
var tab=[]
for (var i=1;i<=20;i++) {
tab.push(i)
}
```

#### Q4. filter(fn(elmt)) est une méthode de Array qui permet de filtrer les éléments d’un tableau en appliquant une fonction qui renvoie un booléen indiquant si oui ou non il faut conserver l’élément. Appliquez filter à tab pour retourner un tableau avec uniquement les multiples de 3.


```javascript
tab.filter(
	(value, index, array) => {
		return value % 3 == 0;
	}
)
```

#### Q5. map(fn(elmt)) est une méthode de Array qui permet d’appliquer la même fonction à tous les éléments d’un tableau. Appliquez map au tableau tab en mettant chaque élément au carré.

```javascript
tab.map(
	(value, index, array) => {
		return value * value;
	}
)
```

#### Q6. reduce(fn(acc,elmt), init ) est une méthode de Array qui permet de “réduire” un tableau en appliquant une fonction à 2 éléments : l’élément courant et l’accumulateur de réponses. Init correspond à la valeur initiale de l’accumulateur. Appliquez reduce au tableau tab pour retourner la somme de tous les éléments du tableau. 

```javascript
tab.reduce(
	(accumulator, currentValue) => {
		return accumulator += currentValue;
	}
)
```

Attention à ne pas confondre : map et reduce sont des fonctions javascript ! Elles existent aussi en Python. mapreduce est une fonction MongoDB. L’idée est similaire, mais a priori elles n’ont rien à voir entre elles.

### Préambule map-reduce

MapReduce est un pattern d’interrogation d’ensembles de données de très grande taille qui s’appuie sur deux fonctions : la première permet d’extraire des informations de chaque document (la mapfunction), la seconde collecte les
retours de la première fonction, par clé identique, pour les agréger en vue du résultat final (la reduceFunction).
L’instruction correspondante s’écrit :

```javascript
db.collection.mapReduce(
	<mapFunction>,
	<reduceFunction>,
	{
		query: <queryFilter>,
		sort: <sortOrder>,
		limit: <number>,
		finalize: <finalizeFunction>,
		out: <collection>
	}
)
```

La mapFunction renvoie des emit(cle, objet) , la reduceFunction(cle, [valeur,...]) renvoie une valeur de même type que ce que la mapFunction émet Documentation ici , Options ici 

Pour ce TP nous utiliserons la collection notes1.json disponible sur Moodle

#### Q7. Récupérez le fichier sur Moodle

#### Q8. Importez ces données dans une collection Mongo

### Exercice 1 : Un premier exemple entièrement détaillé.

On souhaite calculer la somme des notes de tous les étudiants de la collection notes1.

#### Q1. Récupérez dans une variable d, l’un des documents de la collection notes1. Les fonctions suivantes feront référence à ce document.

```javascript
d = db.notes1.findOne()
```

#### Q2. Ecrire une fonction emit() qui prend 2 paramètres clé et valeur et les affiche à la console. Cette fonction n’a qu’un intérêt pédagogique. Elle nous permettra de vérifier le bon fonctionnement.

```javascript
function emit(clef, valeur) {
	console.log(clef)
	console.log(valeur)
}
```

#### Q3. Ecrire une fonction emettre() qui appelle emit avec la clé à null et la valeur souhaitée issue d’un document de note1 identifié par le pointeur this.

```javascript
function emettre(clef, valeur) {
	emit(null, this.valeur);
}
```

#### Q4. Pour tester le bon fonctionnement de cette fonction, appliquez emettre au document d. On utilisera pour cela la fonction apply, qui permet d’appeler une fonction avec l’argument passé comme valeur au pointeur this.

```javascript
emettre.apply(d)
```

#### Q5. Ecrire une fonction reduire(cle, valeurs) à deux arguments, une clé et un tableau, et qui renvoie la somme des valeurs de ce tableau.

```javascript
function reduire(clef, valeurs) {
	return valeurs.reduce(
		(accumulator, currentValue) => {
			return accumulator += currentValue;
		}
	)
}
```

#### Q6. Pour tester, appliquez cette fonction à un tableau d’entiers reduire(null,[1,2,3,4,5])

```javascript
reduire(null,[1,2,3,4,5])
```

#### Q7. Les deux fonctions emettre et reduire ont l’air de bien fonctionner. Utilisez les directement avec la fonction mapreduce de Mongo

```javascript
db.notes1.mapReduce(
	emettre, 
	reduire, 
	{
		out: 
		{
			inline:1
		}
	}
)
```
#### Q8. Ré-écrivez cet appel en une seule instruction en utilisant cette fois des fonctions anonymes.

```javascript
db.notes1.mapReduce(
	function (clef, valeur) {
		emit(null, this.valeur);
	},
	function (clef, valeurs) {
		return valeurs.reduce(
			(accumulator, currentValue) => {
				return accumulator += currentValue;
			}
		)
	},
	{
		out:
		{
			inline: 1
		}
	}
)
```


#### Q9. Relancez la même requête en modifiant la fonction emettre pour qu’elle renvoie this.groupe comme clé au lieu de "rien". Que s’est t-il passé ? pourquoi ?

```javascript
db.notes1.mapReduce(
	function (clef, valeur) {
		emit(this.groupe, this.valeur);
	},
	function (clef, valeurs) {
		return valeurs.reduce(
			(accumulator, currentValue) => {
				return accumulator += currentValue;
			}
		)
	},
	{
		out:
		{
			inline: 1
		}
	}
)
```

mapreduce est une fonction qui s’occupe d’appeler automatiquement une fonction qui emit des données
pour chaque document, puis de regrouper dans un tableau toutes les données ayant la même clé afin de
passer ce tableau à une fonction dont l’objectif est d’en faire un résultat.

### Exercice 2 : Calculs sur l’ensemble de la collection

Quand le calcul s’applique à l’ensemble de la collection (agrégat sans group by), la clé a toujours la même valeur.

Pour l’ensemble de cet exercice , vous donnerez d’abord une solution avec aggregate, puis une solution avec
map-reduce afin de vérifier les résultats.

#### Q1. Affichez le maximum des notes de tous les étudiants

```javascript
db.notes1.aggregate([
	{
		$group:
		{
			_id: null,
			noteMax:
			{
				$max: "$valeur"
			}
		}
	}
])
```

```javascript
db.notes1.mapReduce(
	function (clef, valeur) {
		emit(null, this.valeur);
	},
	function (clef, valeurs) {
		return valeurs.reduce(
			(accumulator, currentValue) => {
				if (currentValue > accumulator) {
					accumulator = currentValue
				}
				return accumulator;
			}
		)
	},
	{
		out:
		{
			inline: 1
		}
	}
)
```

#### Q2. Calculez le nombre de notes dans la collection.


```javascript
db.notes1.aggregate([
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

```javascript
db.notes1.mapReduce(
	function (clef, valeur) {
		emit(null, this.valeur);
	},
	function (clef, valeurs) {
		return valeurs.length;
	},
	{
		out:
		{
			inline: 1
		}
	}
)
```

#### Q3. Affichez en une fois le min et le max des notes de la collection.

```javascript
db.notes1.aggregate([
	{
		$group:
		{
			_id: null,
			min:
			{
				$min: "$valeur"
			},
			max:
			{
				$max: "$valeur"
			}
		}
	}
])
```

```javascript
db.notes1.mapReduce(
	function (clef, valeur) {
		emit(null, this.valeur);
	},
	function (clef, valeurs) {
		return { min: Math.min(...valeurs), max: Math.max(...valeurs) }
	},
	{
		out:
		{
			inline: 1
		}
	}
)
```

#### Q4. Calculez la moyenne des notes dans la collection étudiants

```javascript
db.notes1.aggregate([
	{
		$group:
		{
			_id: null,
			moyNotes:
			{
				$avg: "$valeur"
			}
		}
	}
])
```

```javascript
db.notes1.mapReduce(
	function (clef, valeur) {
		emit(null, this.valeur);
	},
	function (clef, valeurs) {
		const average = valeurs.reduce( ( p, c ) => p + c, 0 ) / valeurs.length;
		return average;
	},
	{
		out:
		{
			inline: 1
		}
	}
)
```

#### Q5. Affichez le prénom le plus court dans la collection (le premier en cas d’égalité).

```javascript
db.notes1.aggregate([
	{
		$project:
		{
			nom: 1,
			prenomSize:
			{
				$strLenCP: "$nom"
			}
		}
	},
	{
		$sort:
		{
			nom: 1,
			prenomSize: 1
		}
	},
	{
		$limit: 1
	}
])
```

```javascript
db.notes1.mapReduce(
	function (clef, nom) {
		emit(null, this.nom);
	},
	function (clef, noms) {
		return noms.reduce(
			(accumulator, currentValue) => {
				if (currentValue < accumulator) {
					accumulator = currentValue;
				}
				return accumulator;
			}
		)
	},
	{
		out:
		{
			inline: 1
		}
	}
)
```

### Exercice 3 : Calcul sur des parties de la collection

Quand le calcul s’applique à des parties de la collection (agrégat avec group by), il est nécessaire d’avoir une clé par
partie.

#### Q1. Calculer le nombre de notes par étudiant

```javascript
db.notes1.aggregate([
	{
		$group:
		{
			_id: "$nom",
			count:
			{
				$sum: 1
			}
		}
	}
])
```

```javascript
db.notes1.mapReduce(
	function (clef, nom) {
		emit(this.nom, null);
	},
	function (clef, noms) {
		return noms.length;
	},
	{
		out:
		{
			inline: 1
		}
	}
)
```

#### Q2. Fournir la distribution des notes de la collection, triée par valeurs.

```javascript
db.notes1.aggregate([
	{
		$group:
		{
			_id: "$valeur",
			count:
			{
				$sum: 1
			}
		}
	},
	{
		$sort:
		{
			_id: 1
		}
	}
])
```

```javascript
db.notes1.mapReduce(
	function (clef, valeur) {
		emit(this.valeur, null);
	},
	function (clef, valeurs) {
		return valeurs.length;
	},
	{
		out:
		{
			inline: 1
		}
	}
);
```

#### Q3. Calculer la moyenne des notes par étudiant

```javascript
db.notes1.aggregate([
	{
		$group:
		{
			_id: "$nom",
			moyenneEtu:
			{
				$avg: "$valeur"
			}
		}
	}
])
```

```javascript
db.notes1.mapReduce(
	function (clef, valeur) {
		emit(this.nom, this.valeur);
	},
	function (clef, valeurs) {
		const average = valeurs.reduce( ( p, c ) => p + c, 0 ) / valeurs.length;
		return average;
	},
	{
		out:
		{
			inline: 1
		}
	}
);
```


A la fin de ce TP détruisez complètement les instances docker que vous avez créés.

Vérifiez que les commandes docker ps --all et docker volume ls ne retournent plus rien