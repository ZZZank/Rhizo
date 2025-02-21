package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
import org.junit.jupiter.api.Test;

/**
 * @author ZZZank
 */
public class DeconstructTest {
    private static final RhinoTest TEST = new RhinoTest("deconstruct");

    @Test
    void arrSimple() {
        TEST.test("arr", """
            const someRandomVar = 0
            const anotherRandomVar = "random"

            const [a, b] = console.testArray
            console.log(a)
            console.log(b)
            """, """
            abc
            def
            """);
    }

    @Test
    void objectSimple() {
        TEST.test("arr", """
            const { a, b } = {
                a: 1,
                b: 234
            };
            console.log(a)
            console.log(b)
            """, """
            1
            234
            """);
    }
}
