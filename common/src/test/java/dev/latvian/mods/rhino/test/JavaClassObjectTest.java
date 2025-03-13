package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
import dev.latvian.mods.rhino.util.ClassWrapper;
import org.junit.jupiter.api.Test;

/**
 * @author ZZZank
 */
public class JavaClassObjectTest {
    private static final RhinoTest TEST = new RhinoTest("class")
        .withBinding("Cla", t -> new Cla());

//    @Test
//    void raw() {
//        TEST.test("raw", """
//            const c = Cla.clazz;
//            console.log(c.valueOf("+33"))""", """
//            33""");
//    }

    @Test
    void wrapped() {
        TEST.test(
            "wrapped", """
                const c = Cla.wrapped;
                console.log(c.valueOf("+33"))""", """
                33"""
        );
    }

    @Test
    void original() {
        TEST.test("original", """
            const c = Cla.clazz;
            console.log(c.getField("MAX_VALUE"))""", """
            public static final int java.lang.Integer.MAX_VALUE
            """);
    }

    @Test
    void subClass() {
        TEST.test("subClass", """
            const C = Cla.wrappedSelf()
            console.log(C.Sub.GOT_IT)
            """, """
            got it""");
    }

    public static class Cla {
        public ClassWrapper<Cla> wrappedSelf() {
            return new ClassWrapper<>(Cla.class);
        }

        public Class<Integer> getClazz() {
            return Integer.class;
        }

        public ClassWrapper<Integer> getWrapped() {
            return new ClassWrapper<>(Integer.class);
        }

        public static class Sub {
            public static final String GOT_IT = "got it";
        }
    }
}
