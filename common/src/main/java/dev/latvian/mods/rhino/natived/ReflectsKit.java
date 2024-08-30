package dev.latvian.mods.rhino.natived;

import dev.latvian.mods.rhino.Kit;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * @author ZZZank
 */
public interface ReflectsKit {
    @NotNull
    static Method[] getDeclaredMethodsSafe(@NotNull Class<?> cl) {
        try {
            return cl.getDeclaredMethods();
        } catch (Throwable t) {
            System.err.println("[Rhino] Failed to get declared methods for " + cl.getName() + ": " + t);
            return new Method[0];
        }
    }

    @NotNull
    static Method[] getMethodsSafe(@NotNull Class<?> cl) {
        try {
            return cl.getMethods();
        } catch (Throwable t) {
            System.err.println("[Rhino] Failed to get public methods for " + cl.getName() + ": " + t);
            return new Method[0];
        }
    }

    @NotNull
    static Constructor<?>[] getConstructorsSafe(@NotNull Class<?> cl) {
        try {
            return cl.getConstructors();
        } catch (Throwable e) {
            System.err.println("[Rhino] Failed to get constructors for " + cl.getName() + ": " + e);
            return new Constructor[0];
        }
    }

    @NotNull
    static Field[] getDeclaredFieldsSafe(@NotNull Class<?> cl) {
        try {
            return cl.getDeclaredFields();
        } catch (Throwable t) {
            System.err.println("[Rhino] Failed to get declared fields for " + cl.getName() + ": " + t);
            return new Field[0];
        }
    }

    @NotNull
    static Field[] getFieldsSafe(@NotNull Class<?> cl) {
        try {
            return cl.getFields();
        } catch (Throwable t) {
            System.err.println("[Rhino] Failed to get public fields for " + cl.getName() + ": " + t);
            return new Field[0];
        }
    }
}
