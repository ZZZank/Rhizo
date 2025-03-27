package dev.latvian.mods.rhino.native_java.type.info;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class BasicClassTypeInfo extends ClassTypeInfo {
	static final Map<Class<?>, BasicClassTypeInfo> CACHE = new ConcurrentHashMap<>();

	BasicClassTypeInfo(Class<?> type) {
		super(type);
	}

	@Override
	public boolean isVoid() {
		return type == Void.class;
	}

	@Override
	public boolean isBoolean() {
		return type == Boolean.class;
	}

	@Override
	public boolean isNumber() {
		return Number.class.isAssignableFrom(type);
	}

	@Override
	public boolean isByte() {
		return type == Byte.class;
	}

	@Override
	public boolean isShort() {
		return type == Short.class;
	}

	@Override
	public boolean isInt() {
		return type == Integer.class;
	}

	@Override
	public boolean isLong() {
		return type == Long.class;
	}

	@Override
	public boolean isFloat() {
		return type == Float.class;
	}

	@Override
	public boolean isDouble() {
		return type == Double.class;
	}

	@Override
	public boolean isCharacter() {
		return type == Character.class;
	}
}
