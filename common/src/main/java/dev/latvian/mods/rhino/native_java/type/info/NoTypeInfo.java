package dev.latvian.mods.rhino.native_java.type.info;

import java.util.function.Consumer;

final class NoTypeInfo implements TypeInfo {
	static final NoTypeInfo INSTANCE = new NoTypeInfo();

	private NoTypeInfo() {}

	@Override
	public Class<?> asClass() {
		return Object.class;
	}

	@Override
	public boolean shouldConvert() {
		return false;
	}

	@Override
	public boolean equals(Object obj) {
		return obj == this;
	}

	@Override
	public int hashCode() {
		return 0;
	}

	@Override
	public String toString() {
		return "?";
	}

	@Override
	public void append(TypeStringContext ctx, StringBuilder sb) {
		sb.append('?');
	}

	@Override
	public TypeInfo asArray() {
		return this;
	}

	@Override
	public TypeInfo withParams(TypeInfo... params) {
		return this;
	}

	@Override
	public void collectContainedComponentClasses(Consumer<Class<?>> collector) {
	}

}
