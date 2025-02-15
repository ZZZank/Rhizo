package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
import dev.latvian.mods.rhino.test.impl.generic.GenericObject;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestMethodOrder;

import java.lang.reflect.Modifier;

@SuppressWarnings("unused")
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class MiscTests {
	public static final RhinoTest TEST = new RhinoTest("misc");

	static {
		TEST.test("init", """
			const testObject = {
				a: -39, b: 2, c: 3439438
			}
			let testList = console.testList
			
			for (let string of testList) {
				console.info(string)
			}
			
			shared.testObject = testObject
			shared.testList = testList
			""", """
			abc
			def
			ghi
			""");
	}

	@Test
	public void testFunctionAssignment() {
		TEST.test("functionAssignment",
			"""
				let x = () => {};
				x.abc = 1;
				console.info(x.abc);
				""",
			"1"
		);
	}

	@Test
	public void testDelete() {
		TEST.test("delete", "let x = {a: 1}; delete x.a; console.info(x.a);", "undefined");
	}

	@Test
	public void enums() {
		TEST.test("enums", """
			console.theme = 'Dark'
			console.info(console.theme == 'DaRK')
			""", """
			Set theme to DARK
			true
			""");
	}

	@Test
	@Order(4)
	public void keysValuesEntries() {
		TEST.test("keysValuesEntries", """
			console.info(Object.keys(shared.testObject))
			console.info(Object.values(shared.testObject))
			console.info(Object.entries(shared.testObject))
			""", """
			['a', 'b', 'c']
			[-39, 2, 3439438]
			[['a', -39], ['b', 2], ['c', 3439438]]
			""");
	}

	@Test
	@Order(4)
	public void deconstruction() {
		TEST.test("deconstruction", """
			const entries = Object.entries(shared.testObject)
			for (let [key, value] of entries) {
				console.info(`${key} : ${value}`)
			}
			""", """
			a : -39
			b : 2
			c : 3439438
			""");
	}

	@Test
	@Order(4)
	public void typeWrappers() {
		TEST.test("typeWrappers", """
			console.printMaterial('wood')
			console.printMaterial('stone')
			console.printMaterial('wood')
			""", """
			wood#0037c6ad
			stone#068af865
			wood#0037c6ad
			""");
	}

	@Test
	public void jsonStringifyWithNestedArrays() {
		TEST.test("jsonStringifyWithNestedArrays", """
			const thing = {nested: [1, 2, 3]};
			console.info(JSON.stringify(thing));
			""", "{\"nested\":[1.0,2.0,3.0]}");
	}

	@Test
	public void types() {
		for (var method : GenericObject.class.getDeclaredMethods()) {
			if (!Modifier.isStatic(method.getModifiers())) {
				GenericObject.testing = method.getName();
				GenericObject.test(method.getName(), method.getGenericReturnType());
			}
		}

		GenericObject.testing = "";
	}
}
