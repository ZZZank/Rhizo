package dev.latvian.mods.rhino.native_java.type.info;

import lombok.val;

import java.util.IdentityHashMap;
import java.util.Map;

public class InterfaceTypeInfo extends ClassTypeInfo {
	static final Map<Class<?>, InterfaceTypeInfo> CACHE = new IdentityHashMap<>();

	public static final byte B_UNKNOWN = -1;
	public static final byte B_TRUE = 1;
	public static final byte B_FALSE = 0;

	private byte functional;

	InterfaceTypeInfo(Class<?> type) {
		this(type, B_UNKNOWN);
	}

	InterfaceTypeInfo(Class<?> type, byte functional) {
		super(type);
		this.functional = functional;
	}

	@Override
	public boolean isFunctionalInterface() {
		if (functional < 0) {
			functional = B_FALSE;

			try {
				if (asClass().isAnnotationPresent(FunctionalInterface.class)) {
					functional = B_TRUE;
				} else {
					int count = 0;

					for (val method : asClass().getMethods()) {
						if (!method.isDefault() && !method.isSynthetic() && !method.isBridge()) {
							count++;
						}

						if (count > 1) {
							break;
						}
					}

					if (count == 1) {
						functional = B_TRUE;
					}
				}
			} catch (Throwable ex) {
				ex.printStackTrace();
			}
		}

		return functional > 0;
	}

	@Override
	public boolean isInterface() {
		return true;
	}
}
