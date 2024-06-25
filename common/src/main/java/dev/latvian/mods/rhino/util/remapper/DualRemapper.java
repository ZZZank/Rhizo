package dev.latvian.mods.rhino.util.remapper;

import lombok.val;
import org.jetbrains.annotations.NotNull;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Objects;

/**
 * @author ZZZank
 */
public class DualRemapper implements Remapper {

    private final Remapper first;
    private final Remapper second;

    public DualRemapper(@NotNull Remapper first, @NotNull Remapper second) {
        Objects.requireNonNull(first);
        Objects.requireNonNull(second);
        this.first = first;
        this.second = second;
    }

    @Override
    public String remapClass(Class<?> from) {
        val remap1 = first.remapClass(from);
        return remap1.isEmpty() ? second.remapClass(from) : remap1;
    }

    @Override
    public String remapMethod(Class<?> from, Method method) {
        val remap1 = first.remapMethod(from, method);
        return remap1.isEmpty() ? second.remapMethod(from, method) : remap1;
    }

    @Override
    public String remapField(Class<?> from, Field field) {
        val remap1 = first.remapField(from, field);
        return remap1.isEmpty() ? second.remapField(from, field) : remap1;
    }

    @Override
    public String unmapClass(String from) {
        val remap1 = first.unmapClass(from);
        return remap1.isEmpty() ? second.unmapClass(from) : remap1;
    }
}
