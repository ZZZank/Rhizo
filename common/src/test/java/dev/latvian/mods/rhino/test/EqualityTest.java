package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
import org.junit.jupiter.api.Test;

/**
 * @author ZZZank
 */
public class EqualityTest {
    public static final RhinoTest TEST = new RhinoTest("equality");

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
    public void enumsShallow() {
        TEST.test("enumShallow", """
			console.theme = 'light'
			console.info(console.theme === 'LIGht')
			""", """
			Set theme to LIGHT
			false
			""");
    }
}
