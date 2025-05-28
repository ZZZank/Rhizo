package dev.latvian.mods.rhino.test;

import dev.latvian.mods.rhino.test.impl.ExampleAbstractClass;
import dev.latvian.mods.rhino.test.impl.base.RhinoTest;
import org.junit.jupiter.api.Test;

import java.util.function.Supplier;

/**
 * @author ZZZank
 */
public class JavaAdapterTest {
    private static final RhinoTest TEST = new RhinoTest("JavaAdapterTest")
        .withBinding(Supplier.class.getSimpleName(), t -> Supplier.class)
        .withBinding("Abstract", t -> ExampleAbstractClass.class);

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
}
