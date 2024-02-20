package dev.latvian.mods.rhino.util;

import java.util.Collections;

public abstract class BackportUtil {
    
    public static String repeat(String str, int times) {
        return String.join("", Collections.nCopies(times, str));
    }

    public static String descriptorString(Class<?> clazz) {
        if (clazz.isPrimitive()) {
            return String.valueOf(BackportWrapper.forPrimitiveType(clazz).basicTypeChar());
        }

        if (clazz.isArray()) {
            return "[" + descriptorString(clazz.getComponentType());
        /*MC1.16, which is devleoped under Java8, is impossible to have `hidden` class defined here
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
        }
        String name = clazz.getName().replace('.', '/');
        return new StringBuilder(name.length() + 2)
                .append('L')
                .append(name)
                .append(';')
                .toString();
    }
}
