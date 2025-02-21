package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.test.impl.MethodOverloads;
import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
import org.junit.jupiter.api.Test;

/**
 * @author ZZZank
 */
public class MethodOverloadTest {
    private static final RhinoTest TEST = new RhinoTest("method_overloads")
        .withBinding("overloads", (t) -> new MethodOverloads(t.console))
        .withTypeWrapper(typeWrappers -> {
            typeWrappers.register(String.class, String::valueOf);
        });

    @Test
    void functionOrObject() {
        TEST.test("functionOrObject", """
            overloads.f1("yes")
            overloads.f1(s => {})""", """
            f1.string
            f1.function""");
    }

    @Test
    void functionOrTypeWrapper() {
        TEST.test("functionOrObject", """
            overloads.f1("yes")
            overloads.f1(s => {})""", """
            f1.string
            f1.function""");
    }
}
