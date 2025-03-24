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
		TEST.test("delete", """
            let x = {a: 1}; delete x.a; console.info(x.a);""", """
            undefined"""
        );
	}

	/**
	 * @see dev.latvian.mods.rhino.NativeArray#TO_SOURCE
	 */
//	@Test
//	@Order(4)
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
	public void jsonStringifyWithNestedArrays() {
		TEST.test("jsonStringifyWithNestedArrays", """
			const thing = {nested: [false, 1.2, 3.4, "56+"]};
			console.info(JSON.stringify(thing));
			""", """
            {"nested":[false,1.2,3.4,"56+"]}"""
        );
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
