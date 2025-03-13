package dev.latvian.mods.rhino.util;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;

import java.util.Objects;

/**
 * @author ZZZank
 */
public class ClassWrapper<T> implements CustomJavaObjectWrapper.New {
    private final Class<T> clazz;

    public ClassWrapper(Class<T> type) {
        this.clazz = Objects.requireNonNull( type);
    }

    @Override
    public Scriptable wrapAsJavaObject(Context cx, Scriptable scope, TypeInfo target) {
        return new NativeJavaClass(cx, scope, clazz);
    }
}
