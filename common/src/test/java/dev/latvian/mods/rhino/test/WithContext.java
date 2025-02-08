package dev.latvian.mods.rhino.test;

import com.github.bsideup.jabel.Desugar;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;

import java.util.Objects;

@Desugar
public record WithContext<T>(Context cx, T value) {
	public static WithContext<?> of(Context cx, Object from, TypeInfo target) {
		var type = target.param(0);

		if (type.shouldConvert()) {
			return new WithContext<>(cx, Context.jsToJava(cx, from, type));
		}

		return new WithContext<>(cx, from);
	}

	@Override
	public int hashCode() {
		return value == null ? 0 : value.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o instanceof WithContext<?> wc && Objects.equals(value, wc.value);
	}

	@Override
	public String toString() {
		return "W[" + value + "]";
	}
}
