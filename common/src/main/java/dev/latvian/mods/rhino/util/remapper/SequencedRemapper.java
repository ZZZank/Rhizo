package dev.latvian.mods.rhino.util.remapper;

import lombok.val;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class SequencedRemapper implements Remapper {

    private final Remapper[] seq;

    public SequencedRemapper(Remapper... remappers) {
        this.seq = remappers;
    }

    @Override
    public String remapMethod(Class<?> from, Method method) {
        String mapped;
        for (val remapper : seq) {
            mapped = remapper.remapMethod(from, method);
            if (!mapped.isEmpty()) {
                return mapped;
            }
        }
        return NOT_REMAPPED;
    }

    @Override
    public String remapField(Class<?> from, Field field) {
        String mapped;
        for (val remapper : seq) {
            mapped = remapper.remapField(from, field);
            if (!mapped.isEmpty()) {
                return mapped;
            }
        }
        return NOT_REMAPPED;
    }

    @Override
    public String remapClass(Class<?> from) {
        String mapped;
        for (val remapper : seq) {
            mapped = remapper.remapClass(from);
            if (!mapped.isEmpty()) {
                return mapped;
            }
        }
        return NOT_REMAPPED;
    }

    @Override
    public String unmapClass(String from) {
        String mapped;
        for (val remapper : seq) {
            mapped = remapper.unmapClass(from);
            if (!mapped.isEmpty()) {
                return mapped;
            }
        }
        return NOT_REMAPPED;
    }
}
