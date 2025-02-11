package dev.latvian.mods.rhino.util.wrap;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;

import java.util.Objects;

/**
 * @author ZZZank
 */
public final class DynamicWrapper<T> implements TypeWrapper<T> {
    public final TypeWrapperValidator validator;
    public final TypeWrapper.Always<T> wrapper;

    public DynamicWrapper(TypeWrapperValidator validator, TypeWrapper.Always<T> wrapper) {
        this.validator = Objects.requireNonNull(validator);
        this.wrapper = Objects.requireNonNull(wrapper);
    }

    @Override
    public boolean canWrap(Context cx, Object from, TypeInfo target) {
        return validator.canWrap(cx, from, target);
    }

    @Override
    public T wrap(Context cx, Object o, TypeInfo target) {
        return wrapper.wrap(cx, o, target);
    }
}
