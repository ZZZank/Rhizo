package dev.latvian.mods.rhino.native_java.type.info.js;

import dev.latvian.mods.rhino.ScriptRuntime;
import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import dev.latvian.mods.rhino.native_java.type.info.TypeStringContext;

import java.util.function.Consumer;

// "abc"
public record JSStringConstantTypeInfo(String constant) implements TypeInfo {
	public static final JSStringConstantTypeInfo EMPTY = new JSStringConstantTypeInfo("");

	@Override
	public Class<?> asClass() {
		return TypeInfo.class;
	}

	@Override
	public String toString() {
		return ScriptRuntime.escapeAndWrapString(constant);
	}

	@Override
	public void append(TypeStringContext ctx, StringBuilder sb) {
		sb.append(ScriptRuntime.escapeAndWrapString(constant));
	}

	@Override
	public void collectContainedComponentClasses(Consumer<Class<?>> collector) {
	}

}
