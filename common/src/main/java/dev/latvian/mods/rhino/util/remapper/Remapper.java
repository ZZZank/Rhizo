package dev.latvian.mods.rhino.util.remapper;


import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Remapper {

	static boolean isRemapped(String mapped) {
		return mapped.isEmpty();
	}

	/**
	 * used as the return value of Remapper when the Remapper does not remap the input
	 */
	String NOT_REMAPPED = "";

	/**
	 * @return a string holding remapped class name, or an empty string if not remapped
	 */
	default String remapClass(Class<?> from) {
		return NOT_REMAPPED;
	}

	/**
	 * @param from name of remapped class
	 * @return the original class name
	 */
	default String unmapClass(String from) {
		return NOT_REMAPPED;
	}

	/**
	 * @return a string holding remapped field name, or an empty string if not remapped
	 */
	default String remapField(Class<?> from, Field field) {
		return NOT_REMAPPED;
	}

	/**
	 * @return a string holding remapped method name, or an empty string if not remapped
	 */
	default String remapMethod(Class<?> from, Method method) {
		return NOT_REMAPPED;
	}
}

