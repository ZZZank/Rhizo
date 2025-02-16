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

    /**
     * @param base the object to be compared against
     * @param shallow {@code true} for {@code ===} and {@code !==}, {@code false} for {@code ==} and {@code !=}
     * @return whether the {@code base} and {@code o} is equal
     */
    static boolean checkLeftBased(Object base, Object o, boolean shallow) {
        if (base instanceof SpecialEquality s) {
            return s.specialEquals(o, shallow);
        } else if (base instanceof Enum<?> e) {
            if (o instanceof Enum<?> e2) {
                return e.equals(e2);
            }
            if (!shallow) {
                if (o instanceof Number num) {
                    return e.ordinal() == num.intValue();
                }
                return EnumTypeInfo.getName(base).equalsIgnoreCase(String.valueOf(o));
            }
        }

        return false;
    }
}
