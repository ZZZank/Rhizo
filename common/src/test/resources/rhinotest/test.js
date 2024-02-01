let testScript = (text) => {
	console.info(text)
}

/*
<!-- HTML Comment test -->

let scopes2 = () => {
	var scopes = [];
	let testArray = ['a', 'b']
	for (i of testArray) {
		console.info(`Iterating ${i}`)
		scopes.push(function () {
			return i;
		});
	}
	console.info(scopes)
	console.info(scopes[0]())
	console.info(scopes[1]())
	return (scopes[0]() === "a" && scopes[1]() === "b");
}

console.info(scopes2())
 */

const test = ['abc', 'def', 'ghi']

for (const x of test) {
	console.info(`Test: ${x}`)
}
for (let x of test) {
	console.info(`Test: ${x}`)
}
for (x of test) {
	console.info(`Test: ${x}`)
}

console.info(`Var: ${x}`)