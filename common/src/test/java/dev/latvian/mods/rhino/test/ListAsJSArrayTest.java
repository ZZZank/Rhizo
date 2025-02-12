package dev.latvian.mods.rhino.test;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

/**
 * @author ZZZank
 */
public class ListAsJSArrayTest {
    public static final RhinoTest TEST = new RhinoTest("list_as_js_array");

    @Test
    @Order(1)
    public void init() {
        TEST.test(
            "init", """
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
                """
        );
    }

    @Test
    public void array() {
        TEST.test(
            "array", """
                for (let x of console.testArray) {
                	console.info(x)
                }
                """, """
                abc
                def
                ghi
                """
        );
    }

    @Test
    @Order(2)
    public void arrayLength() {
        TEST.test("arrayLength", """
			console.info('init ' + shared.testList.length)
			shared.testList.add('abcawidawidaiwdjawd')
			console.info('add ' + shared.testList.length)
			shared.testList.push('abcawidawidaiwdjawd')
			console.info('push ' + shared.testList.length)
			""", """
			init 3
			add 4
			push 5
			""");
    }

    @Test
    @Order(3)
    public void popUnshiftMap() {
        TEST.test("popUnshiftMap", """
			console.info('pop ' + shared.testList.pop() + ' ' + shared.testList.length)
			console.info('shift ' + shared.testList.shift() + ' ' + shared.testList.length)
			console.info('map ' + shared.testList.concat(['xyz']).reverse().map(e => e.toUpperCase()).join(" | "))
			""", """
			pop abcawidawidaiwdjawd 4
			shift abc 3
			map XYZ | ABCAWIDAWIDAIWDJAWD | GHI | DEF
			""");
    }
}
