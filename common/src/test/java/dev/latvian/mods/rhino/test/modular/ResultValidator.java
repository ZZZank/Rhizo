package dev.latvian.mods.rhino.test.modular;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.Wrapper;
import org.junit.jupiter.api.Assertions;

/**
 * @author ZZZank
 */
public interface ResultValidator extends Snapshot.Modifier {

    static ResultValidator noError() {
        return (result, error) -> Assertions.assertNull(error, "Expecting no error");
    }

    static ResultValidator result(Object expected) {
        return (result, error) -> Assertions.assertEquals(expected, result);
    }

    static ResultValidator unwrappedResult(Object expected) {
        return (result, error) -> Assertions.assertEquals(expected, Wrapper.unwrapped(result));
    }

    static ResultValidator javaResult(Object expected, Class<?> desiredType) {
        return (result, error) -> Assertions.assertEquals(expected, Context.jsToJava(result, desiredType));
    }

    static ResultValidator javaResult(Object expected) {
        return javaResult(expected, Object.class);
    }

    static ResultValidator error(Class<? extends Exception> type) {
        return (result, error) -> Assertions.assertTrue(type.isInstance(error));
    }

    void validate(Object result, Exception error);

    @Override
    default void modify(Snapshot snapshot) {
        assert snapshot.testFired();
        validate(snapshot.result(), snapshot.error());
    }
}
