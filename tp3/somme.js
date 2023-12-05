var tab = []

for (var i = 1; i <= 20; i++) {
	tab.push(i)
}

// console.log(tab.filter(
// 	(value, index, array) => {
// 		return value % 3 == 0;
// 	}
// ))

console.log(tab.reduce(
	(accumulator, currentValue) => {
		return accumulator += currentValue;
	}
))



