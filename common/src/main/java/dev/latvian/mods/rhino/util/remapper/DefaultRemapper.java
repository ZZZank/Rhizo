package dev.latvian.mods.rhino.util.remapper;

import dev.latvian.mods.rhino.mod.remapper.MinecraftRemapper;
import dev.latvian.mods.rhino.mod.remapper.RemappingHelper;
import dev.latvian.mods.rhino.util.RemapForJS;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

/**
 * Default impl of {@link Remapper}, will apply {@link RemapForJS} and {@link MinecraftRemapper},
 * {@link MinecraftRemapper} is applied only after {@link RemapForJS} fails to remap
 */
public class DefaultRemapper implements Remapper {
    public static final DefaultRemapper INSTANCE = new DefaultRemapper();
    private final MinecraftRemapper minecraft;

    private DefaultRemapper() {
        this.minecraft = RemappingHelper.getMinecraftRemapper();
    }

    @Override
    public String getUnmappedClass(String from) {
        return this.minecraft.getUnmappedClass(from);
    }

    @Override
    public String getMappedClass(Class<?> from) {
        RemapForJS annot = from.getAnnotation(RemapForJS.class);
        if (annot != null) {
            return annot.value();
        }
        var remapped = this.minecraft.getMappedClass(from);
        if (!remapped.isEmpty()) {
            return remapped;
        }
        return "";
    }

    @Override
    public String getMappedField(Class<?> from, Field field) {
        RemapForJS annot = field.getAnnotation(RemapForJS.class);
        if (annot != null) {
            return annot.value();
        }
        var remapped = this.minecraft.getMappedField(from, field);
        if (!remapped.isEmpty()) {
            return remapped;
        }
        return "";
    }

    @Override
    public String getMappedMethod(Class<?> from, Method method) {
        RemapForJS annot = method.getAnnotation(RemapForJS.class);
        if (annot != null) {
            return annot.value();
        }
        var remapped = this.minecraft.getMappedMethod(from, method);
        if (!remapped.isEmpty()) {
			return remapped;
        }
        return "";
    }
}