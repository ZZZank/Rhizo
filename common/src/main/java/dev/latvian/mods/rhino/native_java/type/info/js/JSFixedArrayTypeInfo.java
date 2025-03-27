package dev.latvian.mods.rhino.native_java.type.info.js;

import dev.latvian.mods.rhino.native_java.type.info.TypeInfo;
import dev.latvian.mods.rhino.native_java.type.info.TypeStringContext;

import java.util.List;
import java.util.function.Consumer;

// [string, number]
public record JSFixedArrayTypeInfo(List<JSOptionalParam> types) implements TypeInfo {
	@Override
	public Class<?> asClass() {
		return TypeInfo.class;
	}

	@Override
	public String toString() {
		return TypeStringContext.DEFAULT.toString(this);
	}

	@Override
	public void append(TypeStringContext ctx, StringBuilder sb) {
		sb.append('[');

		for (int i = 0; i < types.size(); i++) {
			if (i != 0) {
				sb.append(',');
				ctx.appendSpace(sb);
			}

			types.get(i).append(ctx, sb);
		}

		sb.append(']');
	}

	@Override
	public void collectContainedComponentClasses(Consumer<Class<?>> collector) {
		for (var type : types) {
			type.type().collectContainedComponentClasses(collector);
		}
	}

	@Override
	public boolean isArray() {
		return true;
	}
}
