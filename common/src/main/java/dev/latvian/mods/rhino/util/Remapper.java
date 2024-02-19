package dev.latvian.mods.rhino.util;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public interface Remapper {
	static String getTypeName(String type) {
		int array = 0;

		while (type.endsWith("[]")) {
			array++;
			type = type.substring(0, type.length() - 2);
		}

		String t;
		switch (type) {
			case "boolean": t = "Z";break;
			case "byte": t = "B";break;
			case "short": t = "S";break;
			case "int": t = "I";break;
			case "long": t = "J";break;
			case "float": t = "F";break;
			case "double": t = "D";break;
			case "char": t = "C";break;
			case "void": t = "V";break;
			default: t = "L" + type.replace('.', '/') + ";";
		};

		return array == 0 ? t : (BackportUtil.repeat("[", array) + t);
	}

	default String getMappedClass(Class<?> from) {
		return "";
	}

	default String getUnmappedClass(String from) {
		return "";
	}

	default String getMappedField(Class<?> from, Field field) {
		return "";
	}

	default String getMappedMethod(Class<?> from, Method method) {
		return "";
	}
}
