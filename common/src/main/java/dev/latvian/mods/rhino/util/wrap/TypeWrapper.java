package dev.latvian.mods.rhino.util.wrap;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;

/**
 * @author ZZZank
 */
public interface TypeWrapper<T> {

    boolean canWrap(Context cx, Object from, TypeInfo target);

    T wrap(Context cx, Object o, TypeInfo target);

    @FunctionalInterface
    interface Always<T> extends TypeWrapper<T> {
        @Override
        default boolean canWrap(Context cx, Object from, TypeInfo target) {
            return true;
        }
    }
}
