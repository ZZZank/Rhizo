package dev.latvian.mods.rhino.mod.remapper;

import com.github.bsideup.jabel.Desugar;
import dev.latvian.mods.rhino.util.JavaPortingHelper;
import dev.latvian.mods.rhino.util.remapper.Remapper;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author ZZZank
 */
public class RhizoRemapper implements Remapper {

    private static RhizoRemapper INSTANCE = null;

    private final Map<String, Clazz> classMap;
    private final Map<Method, String> method2NameDesc;

    private RhizoRemapper() {
        this.classMap = new HashMap<>();
        this.method2NameDesc = new HashMap<>();
        //TODO: load
    }

    public static RhizoRemapper instance() {
        if (INSTANCE == null) {
            INSTANCE = new RhizoRemapper();
        }
        return INSTANCE;
    }

    void acceptClass(String original, String remapped) {
        this.classMap.put(original, new Clazz(original, remapped, new HashMap<>(), new HashMap<>()));
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
        //descriptor caching
        var nameDesc = method2NameDesc.get(method);
        if (nameDesc == null) {
            var sb = new StringBuilder(method.getName()).append('(');
            for (var t : method.getParameterTypes()) {
                sb.append(JavaPortingHelper.descriptorString(t));
            }
            nameDesc = sb.append(JavaPortingHelper.descriptorString(method.getReturnType())).toString();
            method2NameDesc.put(method, nameDesc);
        }
        var mapped = clazz.methods.get(nameDesc);
        if (mapped == null) {
            return NOT_REMAPPED;
        }
        return mapped.remapped;
    }

    @Desugar
    record Clazz(String original, String remapped, Map<String, MethodInfo> methods,
                        Map<String, FieldInfo> fields) {
        void acceptMethod(String nameDesc, String remapped) {
            this.methods.put(nameDesc, new MethodInfo(nameDesc, remapped));
        }

        void acceptField(String original, String remapped) {
            this.fields.put(original, new FieldInfo(original, remapped));
        }
    }

    @Desugar
    record MethodInfo(String nameDesc, String remapped) {
    }

    @Desugar
    record FieldInfo(String original, String remapped) {
    }
}
