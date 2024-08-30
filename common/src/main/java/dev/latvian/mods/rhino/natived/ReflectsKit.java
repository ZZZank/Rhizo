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
    static Method[] getDeclaredMethodsSafe(Class<?> cl) {
        try {
            return cl.getDeclaredMethods();
        } catch (Throwable t) {
            System.err.println("[Rhino] Failed to get declared methods for " + cl.getName() + ": " + t);
            return new Method[0];
        }
    }

    static Constructor<?> @NotNull [] getConstructorsSafe(Class<?> cl) {
        try {
            return cl.getConstructors();
        } catch (Throwable e) {
            System.err.println("[Rhino] Failed to get constructors for " + cl.getName() + ": " + e);
            return Kit.emptyArray();
        }
    }

    static Field[] getDeclaredFieldsSafe(Class<?> cl) {
        try {
            return cl.getDeclaredFields();
        } catch (Throwable t) {
            System.err.println("[Rhino] Failed to get declared fields for " + cl.getName() + ": " + t);
            return Kit.emptyArray();
        }
    }
}
