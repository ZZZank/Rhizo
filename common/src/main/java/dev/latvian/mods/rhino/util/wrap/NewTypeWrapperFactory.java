package dev.latvian.mods.rhino.util.wrap;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;

/**
 * @author ZZZank
 */
@FunctionalInterface
public interface NewTypeWrapperFactory<T> {
    T wrap(Context cx, Object o, TypeInfo target);

    @Deprecated
    default T wrap(Object o) {
        return wrap(Context.getContext(), o, o == null ? TypeInfo.NONE : TypeInfo.of(o.getClass()));
    }
}
