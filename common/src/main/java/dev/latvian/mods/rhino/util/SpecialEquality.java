package dev.latvian.mods.rhino.util;

import dev.latvian.mods.rhino.native_java.type.info.EnumTypeInfo;

/**
 * Implement this on a class to override == != === and !== checks in JavaScript
 */
public interface SpecialEquality {
    default boolean specialEquals(Object o, boolean shallow) {
        return equals(o);
    }

    static boolean check(Object x, Object y, boolean shallow) {
        return checkLeftBased(x, y, shallow) || checkLeftBased(y, x, shallow);
    }

    static boolean checkLeftBased(Object base, Object o, boolean shallow) {
        if (base instanceof SpecialEquality s) {
            return s.specialEquals(o, shallow);
        } else if (base instanceof Enum<?> e) {
            if (o instanceof Number) {
                return e.ordinal() == ((Number) o).intValue();
            }
            return EnumTypeInfo.getName(base).equalsIgnoreCase(String.valueOf(o));
        }

        return false;
    }
}
