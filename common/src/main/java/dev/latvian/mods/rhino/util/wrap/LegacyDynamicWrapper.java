package dev.latvian.mods.rhino.util.wrap;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;

import java.util.Objects;
import java.util.function.Predicate;

/**
 * @author ZZZank
 */
public final class LegacyDynamicWrapper<T> implements TypeWrapper<T> {
    private final Predicate<Object> validator;
    private final TypeWrapperFactory<T> wrapper;

    public LegacyDynamicWrapper(Predicate<Object> validator, TypeWrapperFactory<T> wrapper) {
        this.validator = Objects.requireNonNull(validator);
        this.wrapper = Objects.requireNonNull(wrapper);
    }

    @Override
    public boolean canWrap(Context cx, Object from, TypeInfo target) {
        return validator.test(from);
    }

    @Override
    public T wrap(Context cx, Object o, TypeInfo target) {
        return wrapper.wrap(o);
    }
}
