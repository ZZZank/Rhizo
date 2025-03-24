package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
import dev.latvian.mods.rhino.test.impl.base.TestConsole;
import org.junit.jupiter.api.Test;

import java.util.function.Consumer;

/**
 * only because KubeJS registered a type wrapper for {@link String}
 *
 * @author ZZZank
 */
public class DangerousTypeWrapperTest {
    private static final RhinoTest TEST = new RhinoTest("dangerous_type_wrappers")
        .withBinding("overloads", (t) -> new MethodOverloads(t.console))
        .withTypeWrapper(typeWrappers -> typeWrappers.register(String.class, String::valueOf));
    private static final RhinoTest TEST_NO_WRAPPER = new RhinoTest("dangerous_type_wrappers")
        .withBinding("overloads", (t) -> new MethodOverloads(t.console));

    @Test
    void functionOrObject() {
        TEST.test("functionOrObject", """
            overloads.f1("yes")
            overloads.f1(s => {})""", """
            f1.string
            f1.function""");
    }

    @Test
    void functionOrObjectNoExplicitWrapper() {
        TEST_NO_WRAPPER.test("functionOrObjectNoExplicitWrapper", """
            overloads.f1("yes")
            overloads.f1(s => {})""", """
            f1.string
            f1.function""");
    }

    @Test
    void wrapFunctionObject() {
        TEST.test("wrapFunctionObject", """
            overloads.f2(s => {})""", """
            f2""");
    }

    @Test
    void wrapFunctionObjectNoExplicitWrapper() {
        TEST_NO_WRAPPER.test("wrapFunctionObjectNoExplicitWrapper", """
            overloads.f2(s => {})""", """
            f2""");
    }

    @Test
    public void string2material() {
        TEST.test("string2material", """
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
    void object2string() {
        TEST.test("object2string", """
            overloads.f1(overloads)
            overloads.f1(overloads.thiz())
            """, """
            f1.string
            f1.string
            """);
    }

    /**
     * @author ZZZank
     */
    public static class MethodOverloads {
        private final TestConsole console;

        public MethodOverloads(TestConsole console) {
            this.console = console;
        }

        public void f1(String s) {
            console.info("f1.string");
        }

        public void f1(Consumer<String> s) {
            console.info("f1.function");
        }

        public void f2(String s) {
            console.info("f2");
        }

        public Number get1() {
            return 0;
        }

        public MethodOverloads thiz() {
            return this;
        }
    }
}
