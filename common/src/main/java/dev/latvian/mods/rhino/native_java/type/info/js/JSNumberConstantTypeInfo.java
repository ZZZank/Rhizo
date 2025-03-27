package dev.latvian.mods.rhino.native_java.type.info.js;

import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import dev.latvian.mods.rhino.native_java.type.info.TypeStringContext;

import java.util.function.Consumer;

// 10, -402.01
public record JSNumberConstantTypeInfo(Number number) implements TypeInfo {
	@Override
	public Class<?> asClass() {
		return TypeInfo.class;
	}

	@Override
	public String toString() {
		return number.toString();
	}

	@Override
	public void append(TypeStringContext ctx, StringBuilder sb) {
		sb.append(number);
	}

	@Override
	public void collectContainedComponentClasses(Consumer<Class<?>> collector) {
	}

}
