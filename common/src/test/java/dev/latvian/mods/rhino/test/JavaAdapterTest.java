package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.test.impl.ExampleAbstractClass;
import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
import lombok.val;
import org.apache.commons.lang3.mutable.Mutable;
import org.apache.commons.lang3.mutable.MutableObject;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

/**
 * @author ZZZank
 */
public class JavaAdapterTest {
    private static final RhinoTest TEST = new RhinoTest("JavaAdapterTest")
        .withBinding(Supplier.class.getSimpleName(), t -> Supplier.class)
        .withBinding("Abstract", t -> ExampleAbstractClass.class)
        .withBinding(ExampleInt2StrFunction.class.getSimpleName(), t -> ExampleInt2StrFunction.class);

    @Test
    public void fnInterface() {
        TEST.test("simple", """
            const testSupplier = new JavaAdapter(Supplier, {
                get: function() {
                    console.info("triggered");
                    return "yes";
                }
            });
            testSupplier.get();
            """, "triggered");
    }

    @Test
    public void abstractClass() {
        TEST.test("simple", """
            const testSupplier = new JavaAdapter(Abstract, {
                get: function() {
                    console.info("triggered");
                    return "yes";
                }
            });
            testSupplier.get();
            """, "triggered");
    }

    public interface ExampleInt2StrFunction {
        Mutable<ExampleInt2StrFunction> INSTANCE = new MutableObject<>();

        String apply(int i);
    }

    @Test
    public void wrap() {
        TEST.test("simple", """
            const test = new JavaAdapter(ExampleInt2StrFunction, {
                apply: function(i) {
                    // Rhino should be able to wrap number to string automatically
                    return i;
                }
            });
            ExampleInt2StrFunction.INSTANCE.setValue(test);
            """, "");
        val s = ExampleInt2StrFunction.INSTANCE.getValue().apply(123);
        Assertions.assertEquals("123", s);
    }
}
