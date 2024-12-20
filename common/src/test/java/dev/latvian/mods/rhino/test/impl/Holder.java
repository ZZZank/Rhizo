package dev.latvian.mods.rhino.test.impl;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import lombok.val;

public record Holder<T>(T value) {

	public static Holder of(Context cx, Object from, TypeInfo target) {
		val type = target.param(0);
		if (type.shouldConvert()) {
			return new Holder<>(Context.jsToJava(cx, from, type));
		}
		return new Holder<>(from);
	}
}
