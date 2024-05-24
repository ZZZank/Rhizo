package dev.latvian.mods.rhino.util;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;

public class JavaPortingHelper {

    public static Path ofPath(String first, String... more) {
        return FileSystems.getDefault().getPath(first, more);
    }

    public static String repeat(String str, int times) {
        return String.join("", Collections.nCopies(times, str));
    }

    public static boolean isBlank(String str) {
        return str.trim().isEmpty();
    }

    /*
     * Returns the {@code Class} representing the element type of an array class.
     * If this class does not represent an array class, then this method returns
     * {@code null}.
     */
    private static Class<?> elementType(Class<?> clazz) {
        if (!clazz.isArray()) {
            return null;
        }
        Class<?> c = clazz;
        while (c.isArray()) {
            c = c.getComponentType();
        }
        return c;
    }

    public static String getPackageName(Class<?> clazz) {
        if (clazz == null) {
            return null;
        }
        Class<?> c = clazz.isArray() ? elementType(clazz) : clazz;
        if (c.isPrimitive()) {
            return "java.lang";
        }
        String cn = c.getName();
        int dot = cn.lastIndexOf('.');
        if (dot == -1) {
            return "";
        }
        return cn.substring(0, dot).intern();
    }

    public static String descriptorString(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return switch (clazz.getName()) {
                case "boolean" -> "Z";
                case "byte" -> "B";
                case "short" -> "S";
                case "int" -> "I";
                case "long" -> "J";
                case "float" -> "F";
                case "double" -> "D";
                case "char" -> "C";
                case "void" -> "V";
                default -> throw new IllegalStateException("'clazz' is Primitive Class, but not mapped to a predefined descriptor");
            };
        }

        if (clazz.isArray()) {
            return "[" + JavaPortingHelper.descriptorString(clazz.getComponentType());
        /*hidden class only exists after Java15
        } else if (clazz.isHidden()) {
            String name = clazz.getName();
            int index = name.indexOf('/');
            return new StringBuilder(name.length() + 2)
                .append('L')
                .append(name.substring(0, index).replace('.', '/'))
                .append('.')
                .append(name, index + 1, name.length())
                .append(';')
                .toString();
        */
        } else {
            String name = clazz.getName().replace('.', '/');
            return new StringBuilder(name.length() + 2)
                .append('L')
                .append(name)
                .append(';')
                .toString();
        }
    }
}
