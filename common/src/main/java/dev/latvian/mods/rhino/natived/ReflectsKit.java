package dev.latvian.mods.rhino.natived;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.StringJoiner;

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

    String ARRAY_SUFFIX = "[]";

    static String javaSignature(Class<?> type) {
        if (!type.isArray()) {
            return type.getName();
        }
        int arrayDimension = 0;
        do {
            ++arrayDimension;
            type = type.getComponentType();
        } while (type.isArray());
        val name = type.getName();
        if (arrayDimension == 1) {
            return name.concat(ARRAY_SUFFIX);
        }
        val sb = new StringBuilder(name.length() + arrayDimension * ARRAY_SUFFIX.length());
        sb.append(name);
        while (arrayDimension != 0) {
            --arrayDimension;
            sb.append(ARRAY_SUFFIX);
        }
        return sb.toString();
    }

    /**
     * provide param info for Rhino precise native accessing, e.g. {@code obj["void someMethod(java.lang.Object[],I)"]}
     * where {@code (java.lang.Object[],I)} is from this method
     */
    static String liveConnectSignature(Class<?>[] argTypes) {
        if (argTypes.length == 0) {
            return "()";
        }
        val joiner = new StringJoiner(",", "(", ")");
        for (val argType : argTypes) {
            joiner.add(javaSignature(argType));
        }
        return joiner.toString();
    }
}
