package dev.latvian.mods.rhino.util.remapper;

import dev.latvian.mods.rhino.util.RemapForJS;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * an impl of {@link Remapper} that will check {@link RemapForJS} annotation
 */
public class AnnotatedRemapper implements Remapper {
    public static final AnnotatedRemapper INSTANCE = new AnnotatedRemapper();

    private AnnotatedRemapper() {}

    @Override
    public String remapClass(Class<?> from) {
        RemapForJS remap = from.getAnnotation(RemapForJS.class);
        if (remap != null) {
            return remap.value();
        }
        return NOT_REMAPPED;
    }

    @Override
    public String remapField(Class<?> from, Field field) {
        RemapForJS remap = field.getAnnotation(RemapForJS.class);
        if (remap != null) {
            return remap.value();
        }
        return NOT_REMAPPED;
    }

    @Override
    public String remapMethod(Class<?> from, Method method) {
        RemapForJS remap = method.getAnnotation(RemapForJS.class);
        if (remap != null) {
            return remap.value();
        }
        return NOT_REMAPPED;
    }
}