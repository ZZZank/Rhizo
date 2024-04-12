package dev.latvian.mods.rhino.util.remapper;


import dev.latvian.mods.rhino.util.JavaPortingHelper;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Remapper {

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
	 * @return a string holding remapped class name, or {@code from.getName()} if not remapped
	 */
	default String getMappedClass(Class<?> from) {
		return from.getName();
	}

	/**
	 * @param from name of remapped class
	 * @return the original class name
	 */
	default String getUnmappedClass(String from) {
		return "";
	}

	/**
	 * @return a string holding remapped field name, or {@code field.getName()} if not remapped
	 */
	default String getMappedField(Class<?> from, Field field) {
		return field.getName();
	}

	/**
	 * @return a string holding remapped method name, or {@code method.getName()} if not remapped
	 */
	default String getMappedMethod(Class<?> from, Method method) {
		return method.getName();
	}
}

