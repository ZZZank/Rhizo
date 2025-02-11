package dev.latvian.mods.rhino.util.wrap;


import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;

@FunctionalInterface
public interface TypeWrapperValidator {
    TypeWrapperValidator ALWAYS = (cx, from, target) -> true;

    boolean canWrap(Context cx, Object from, TypeInfo target);

    @FunctionalInterface
    interface Old extends TypeWrapperValidator {

        boolean canWrap(Object o);

        @Override
        default boolean canWrap(Context cx, Object from, TypeInfo target) {
            return canWrap(from);
        }
    }
}