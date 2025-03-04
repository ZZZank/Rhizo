package dev.latvian.mods.rhino.util.wrap;


import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;

@FunctionalInterface
public interface TypeWrapperValidator {
    TypeWrapperValidator ALWAYS = (cx, from, target) -> true;
    TypeWrapperValidator NEVER = (cx, from, target) -> false;

    boolean canWrap(Context cx, Object from, TypeInfo target);
}