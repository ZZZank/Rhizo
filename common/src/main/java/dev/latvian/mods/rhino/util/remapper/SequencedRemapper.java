package dev.latvian.mods.rhino.util.remapper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SequencedRemapper implements Remapper {

    private final Remapper[] seq;

    public SequencedRemapper(Remapper... remappers) {
        this.seq = remappers;
    }

    @Override
    public String getMappedMethod(Class<?> from, Method method) {
        String mapped;
        for (final Remapper remapper : seq) {
            mapped = remapper.getMappedMethod(from, method);
            if (!mapped.isEmpty()) {
                return mapped;
            }
        }
        return NOT_REMAPPED;
    }

    @Override
    public String getMappedField(Class<?> from, Field field) {
        String mapped;
        for (final Remapper remapper : seq) {
            mapped = remapper.getMappedField(from, field);
            if (!mapped.isEmpty()) {
                return mapped;
            }
        }
        return NOT_REMAPPED;
    }

    @Override
    public String getMappedClass(Class<?> from) {
        String mapped;
        for (final Remapper remapper : seq) {
            mapped = remapper.getMappedClass(from);
            if (!mapped.isEmpty()) {
                return mapped;
            }
        }
        return NOT_REMAPPED;
    }

    @Override
    public String getUnmappedClass(String from) {
        String mapped;
        for (final Remapper remapper : seq) {
            mapped = remapper.getUnmappedClass(from);
            if (!mapped.isEmpty()) {
                return mapped;
            }
        }
        return NOT_REMAPPED;
    }
}
