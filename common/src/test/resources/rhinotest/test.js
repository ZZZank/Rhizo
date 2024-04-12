let testScript = (text) => {
	console.info(text)
}

/*
<!-- HTML Comment test -->
*/
let scopes2 = () => {
	var scopes = [];
	let testArray = ['a', 'b']
	for (let i of testArray) {
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

const test = ['abc', 'def', 'ghi']
let x = 1

for (const x = 1; x != 10; ) {
	console.info("Test const; once")
	break
}
for (const x of test) {
	x=x+"const"
	console.info(`Test const of: ${x}`)
}
for (let x of test) {
	console.info(`Test let: ${x}`)
}
for (var x of test) {
	x=x+"var"
	console.info(`Test vaar: ${x}`)
}
/**leaking references
for (x of test) {
	x=x+"nope"
	console.info(`Test "nope": ${x}`)
}
*/

console.info(`Var: ${x}`)