package dev.latvian.mods.rhino.util.remapper;


import dev.latvian.mods.rhino.util.JavaPortingHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Remapper {

	/**
	 * used as the return value of Remapper when the Remapper does not remap the input
	 */
	String NOT_REMAPPED = "";

	static String getTypeName(String type) {
		int array = 0;
		while (type.endsWith("[]")) {
			array++;
			type = type.substring(0, type.length() - 2);
		}

		String t = switch (type) {
			case "boolean" -> "Z";
			case "byte" -> "B";
			case "short" -> "S";
			case "int" -> "I";
			case "long" -> "J";
			case "float" -> "F";
			case "double" -> "D";
			case "char" -> "C";
			case "void" -> "V";
			default -> "L" + type.replace('.', '/') + ";";
		};

		return array == 0 ? t : (JavaPortingHelper.repeat("[", array) + t);
	}

	/**
	 * @return a string holding remapped class name, or an empty string if not remapped
	 */
	default String getMappedClass(Class<?> from) {
		return NOT_REMAPPED;
	}

	/**
	 * @param from name of remapped class
	 * @return the original class name
	 */
	default String getUnmappedClass(String from) {
		return NOT_REMAPPED;
	}

	/**
	 * @return a string holding remapped field name, or an empty string if not remapped
	 */
	default String getMappedField(Class<?> from, Field field) {
		return NOT_REMAPPED;
	}

	/**
	 * @return a string holding remapped method name, or an empty string if not remapped
	 */
	default String getMappedMethod(Class<?> from, Method method) {
		return NOT_REMAPPED;
	}
}

