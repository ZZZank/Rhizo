package dev.latvian.mods.rhino.util;

import dev.latvian.mods.rhino.util.remapper.Remapper;

import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.Collections;
import java.util.Map;

public class JavaPortingHelper {

//    public static final Map IMMUTABLE_MAP = Collections.emptyMap();

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
        String pn = null;
        Class<?> c = clazz.isArray() ? elementType(clazz) : clazz;
        if (c.isPrimitive()) {
            pn = "java.lang";
        } else {
            String cn = c.getName();
            int dot = cn.lastIndexOf('.');
            pn = (dot != -1) ? cn.substring(0, dot).intern() : "";
        }
        return pn;
    }

    public static String descriptorString(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return Remapper.getTypeName(clazz.getName());
        }

        if (clazz.isArray()) {
            return "[" + clazz.getComponentType().descriptorString();
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
