package dev.latvian.mods.rhino.test;

import org.junit.jupiter.api.Test;

/**
 * @author ZZZank
 */
public class RegexTest {
    public static final RhinoTest TEST = new RhinoTest("regex");

    @Test
    void basic() {
        TEST.test("basic", """
            const reg = /^prefi/
            const test = reg.test.bind(reg)
            
            console.log(test("prefi yes"), test("prefNo"), test("andPref"))""", "true false false");
    }
}
