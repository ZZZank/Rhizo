package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
import org.junit.jupiter.api.Test;

/**
 * @author ZZZank
 */
public class NativeWithTest {
    public static final RhinoTest TEST = new RhinoTest("with");

    @Test
    public void dontError() {
        TEST.test("mixed", """
			const exampleArray = [1, "2", 3, false]
			
			const someFn = (o) => {
				if (o == undefined) {
					return false
				}
				return typeof o == "boolean"
			}
			
			for (let elem in exampleArray) {
				if (!someFn(elem)) {
					continue
				}
				console.log(elem)
			}
			""", """
			""");
    }

    @Test
    public void dontError2() {
        TEST.test("dontError2", """
			const exampleArray = [1, "2", 3, false]
			
			function someFn(o) {
				if (o == undefined) {
					return false
				}
				return typeof o == "boolean"
			}
			
			for (let elem in exampleArray) {
				if (!someFn(elem)) {
					continue
				}
				console.log(elem)
			}
			""", """
			""");
    }
}
