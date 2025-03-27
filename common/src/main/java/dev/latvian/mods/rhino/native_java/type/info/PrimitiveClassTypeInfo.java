package dev.latvian.mods.rhino.native_java.type.info;

import org.jetbrains.annotations.Nullable;

public final class PrimitiveClassTypeInfo extends ClassTypeInfo {
	private final Object defaultValue;

	public PrimitiveClassTypeInfo(Class<?> type, @Nullable Object defaultValue) {
		super(type);
		this.defaultValue = defaultValue;
	}

	@Override
	public boolean isPrimitive() {
		return true;
	}

	@Override
	public boolean isVoid() {
		return type == Void.TYPE;
	}

	@Override
	public boolean isBoolean() {
		return type == Boolean.TYPE;
	}

	@Override
	public boolean isNumber() {
		return Number.class.isAssignableFrom(type);
	}

	@Override
	public boolean isByte() {
		return type == Byte.TYPE;
	}

	@Override
	public boolean isShort() {
		return type == Short.TYPE;
	}

	@Override
	public boolean isInt() {
		return type == Integer.TYPE;
	}

	@Override
	public boolean isLong() {
		return type == Long.TYPE;
	}

	@Override
	public boolean isFloat() {
		return type == Float.TYPE;
	}

	@Override
	public boolean isDouble() {
		return type == Double.TYPE;
	}

	@Override
	public boolean isCharacter() {
		return type == Character.TYPE;
	}

	@Override
	@Nullable
	public Object createDefaultValue() {
		return defaultValue;
	}
}
