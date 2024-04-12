package dev.latvian.mods.rhino.util.remapper;


import java.lang.reflect.AnnotatedElement;
import java.lang.reflect.Field;
import java.lang.reflect.Member;
import java.lang.reflect.Method;
import java.util.Collections;

public interface Remapper {

	/**
	 * @deprecated please use more concrete variants,
	 * @param from the class that {@code member} belongs to
	 * @param member a Java member, method/field
	 * @return remapped name
	 * @see Remapper#getMappedField(Class, Field)
	 * @see Remapper#getMappedMethod(Class, Method)
	 */
	default String remap(Class<?> from, Member member) {
		if (member instanceof AnnotatedElement annotatedElement) {
            RemapForJS remap = annotatedElement.getAnnotation(RemapForJS.class);
			if (remap != null) {
				return remap.value();
			}
		}
		return "";
	}
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

		return array == 0 ? t : (String.join("", Collections.nCopies(array, "[")) + t);
	}

	/**
	 * @return a string holding remapped class name, or an empty string if not remapped
	 */
	default String getMappedClass(Class<?> from) {
		return "";
	}

	/**
	 * @param from name of remapped class
	 * @return the original class name
	 */
	default String getUnmappedClass(String from) {
		return "";
	}

	/**
	 * @return a string holding remapped field name, or an empty string if not remapped
	 */
	default String getMappedField(Class<?> from, Field field) {
		return "";
	}

	/**
	 * @return a string holding remapped method name, or an empty string if not remapped
	 */
	default String getMappedMethod(Class<?> from, Method method) {
		return "";
	}
}

