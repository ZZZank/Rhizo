package dev.latvian.mods.rhino.native_java.type.info;

import java.util.Set;

public abstract class ClassTypeInfo extends TypeInfoBase {
	protected final Class<?> type;
	private Set<Class<?>> typeSet;

	ClassTypeInfo(Class<?> type) {
		this.type = type;
	}

	@Override
	public final Class<?> asClass() {
		return type;
	}

	@Override
	public boolean is(TypeInfo info) {
		if (info instanceof ParameterizedTypeInfo) {
			return info.is(this);
		}
		return super.is(info);
	}

	@Override
	public boolean shouldConvert() {
		return type != Object.class;
	}

	@Override
	public int hashCode() {
		return type.hashCode();
	}

	@Override
	public boolean equals(Object o) {
		return o == this || o instanceof ClassTypeInfo t && type == t.type;
	}

	@Override
	public String toString() {
		return type.getName();
	}

	@Override
	public void append(TypeStringContext ctx, StringBuilder sb) {
		ctx.appendClassName(sb, this);
	}

}
