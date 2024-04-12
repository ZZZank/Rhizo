let testScript = (text) => {
	console.info(text)
}

const sharedArr = [false, 0, "2nd", 3.0]

const tests = {
	"same name but different scope": () => {
		let x = 1
		console.info(`before: ${x}`)
		{
			const x = "wow"
			console.info(`in scope: ${x}`)
		}
		console.info(`after: ${x}`)
	},
	"scopes": () => {
		/*
		<!-- HTML Comment test -->
		*/
		const scopes2 = () => {
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
		console.info(scopes2)
	},
	"const in loop" :() => {
		const x = 1
		/**cannot compile
		for (const x of sharedArr) {
			console.info(`Test const: ${x}`)
		}
		*/
		throw "cannot compile"
		console.info(`after x: ${x}`)
	},
	"loop reference": () => {
		let x = 1
		console.info(`x=${x}`)
		const test = ['abc', 'def', 'ghi']
		for (let x of test) {
			console.info(`Test let: ${x}`)
		}
		for (x of test) {
			console.info(`Test nope: ${x}`)
		}
		console.info(`Var: ${x}`)
	}
}

let passCount = 0
const failed = []
Object.entries(tests).forEach(([key, value])=>{
	console.info(`[?]Running test: ${key}`)
	try {
		value()
		passCount += 1
	} catch (e) {
		console.info(`[x]Error occured when running test: ${key}`)
		failed.push({
			name: key,
			err: e
		})
	}
	console.info(`[y]Finish test: ${key}`)
})
let failCount = failed.length
let totalCount = passCount + failCount
if (totalCount == passCount) {
	console.info(`[y]${passCount}/${totalCount} passed`)
} else {
	console.info(`[x]${passCount}/${totalCount} passed`)
	console.info("[x]failed:")
	failed.forEach(f=>{
		console.info("    "+f.name)
		console.info("        err:"+f.err)
	})
}
