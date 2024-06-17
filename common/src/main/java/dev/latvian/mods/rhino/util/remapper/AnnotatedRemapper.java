package dev.latvian.mods.rhino.util.remapper;

import dev.latvian.mods.rhino.annotations.RemapPrefixForJS;
import dev.latvian.mods.rhino.util.RemapForJS;
import lombok.val;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * an impl of {@link Remapper} that will check {@link RemapForJS} and {@link RemapPrefixForJS} annotations
 */
public class AnnotatedRemapper implements Remapper {
    public static final AnnotatedRemapper INSTANCE = new AnnotatedRemapper();

    private AnnotatedRemapper() {}

    @Override
    public String remapClass(Class<?> from) {
        val remap = from.getAnnotation(RemapForJS.class);
        if (remap != null) {
            return remap.value();
        }
        return NOT_REMAPPED;
    }

    @Override
    public String remapField(Class<?> from, Field field) {
        val remap = field.getAnnotation(RemapForJS.class);
        if (remap != null) {
            return remap.value();
        }
        val remapPrefix = from.getAnnotation(RemapPrefixForJS.class);
        if (remapPrefix != null) {
            val prefix = remapPrefix.value();
            val original = field.getName();
            if (original.startsWith(prefix)) {
                return original.substring(prefix.length());
            }
        }
        return NOT_REMAPPED;
    }

    @Override
    public String remapMethod(Class<?> from, Method method) {
        val remap = method.getAnnotation(RemapForJS.class);
        if (remap != null) {
            return remap.value();
        }
        val remapPrefix = from.getAnnotation(RemapPrefixForJS.class);
        if (remapPrefix != null) {
            val prefix = remapPrefix.value();
            val original = method.getName();
            if (original.startsWith(prefix)) {
                return original.substring(prefix.length());
            }
        }
        return NOT_REMAPPED;
    }
}