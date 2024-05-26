package dev.latvian.mods.rhino.mod.remapper;

import com.github.bsideup.jabel.Desugar;
import com.google.common.collect.ArrayListMultimap;
import com.google.common.collect.Multimap;
import dev.latvian.mods.rhino.util.JavaPortingHelper;
import dev.latvian.mods.rhino.util.remapper.Remapper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 * @author ZZZank
 */
public class RhizoRemapper implements Remapper {

    private static RhizoRemapper INSTANCE = null;

    private final Map<String, Clazz> classMap;

    private RhizoRemapper() {
        this.classMap = new HashMap<>();
        //TODO: load
    }

    public static RhizoRemapper instance() {
        if (INSTANCE == null) {
            INSTANCE = new RhizoRemapper();
        }
        return INSTANCE;
    }

    void acceptClass(String original, String remapped) {
        this.classMap.put(original, new Clazz(original, remapped, ArrayListMultimap.create(), new HashMap<>()));
    }

    @Override
    public String getMappedClass(Class<?> from) {
        var clz = getClazzFiltered(from);
        if (clz == null) {
            return NOT_REMAPPED;
        }
        return clz.remapped;
    }

    private @Nullable Clazz getClazzFiltered(Class<?> from) {
        if (from == null || from == Object.class || JavaPortingHelper.getPackageName(from).startsWith("java.")) {
            return null;
        }
        return classMap.get(from.getName());
    }

    @Override
    public String getUnmappedClass(String from) {
        throw new AssertionError("not implemented yet");
    }

    @Override
    public String getMappedField(Class<?> from, Field field) {
        var clazz = getClazzFiltered(from);
        if (clazz == null) {
            return NOT_REMAPPED;
        }
        var fInfo = clazz.fields.get(field.getName());
        if (fInfo == null) {
            return NOT_REMAPPED;
        }
        return fInfo.remapped;
    }

    @Override
    public String getMappedMethod(Class<?> from, Method method) {
        var clazz = getClazzFiltered(from);
        if (clazz == null) {
            return NOT_REMAPPED;
        }
        var methods = clazz.methods.get(method.getName());
        if (methods.isEmpty()) {
            return NOT_REMAPPED;
        }
        var sb = new StringBuilder().append('(');
        for (var t : method.getParameterTypes()) {
            sb.append(JavaPortingHelper.descriptorString(t));
        }
        var paramDesc = sb.toString();
        for (var m : methods) {
            if (m.paramDescriptor.equals(paramDesc)) {
                return m.remapped;
            }
        }
        return NOT_REMAPPED;
    }

    @Desugar
    record Clazz(String original, String remapped, Multimap<String, MethodInfo> methods,
                 Map<String, FieldInfo> fields) {
        void acceptMethod(String original, String descriptor, String remapped) {
            int rightBracket = descriptor.lastIndexOf(')');
            if (rightBracket < 0) {
                throw new IllegalArgumentException(String.format("arg 'paramDescriptor' with value '%s' not valid",
                    descriptor
                ));
            }
            this.methods.put(original, new MethodInfo(original, descriptor.substring(0, rightBracket), remapped));
        }

        void acceptField(String original, String remapped) {
            this.fields.put(original, new FieldInfo(original, remapped));
        }
    }

    @Desugar
    record MethodInfo(String original, String paramDescriptor, String remapped) {
    }

    @Desugar
    record FieldInfo(String original, String remapped) {
    }
}
