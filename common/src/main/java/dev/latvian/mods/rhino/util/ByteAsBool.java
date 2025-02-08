package dev.latvian.mods.rhino.util;

import lombok.experimental.UtilityClass;

/**
 * @author ZZZank
 */
@UtilityClass
public class ByteAsBool {

    public final byte UNKNOWN = -1;
    public final byte FALSE = 0;
    public final byte TRUE = 1;

    public byte fromBool(boolean b) {
        return b ? TRUE : FALSE;
    }

    public boolean isUnknown(byte b) {
        return b < 0;
    }

    public boolean isKnown(byte b) {
        return b >= 0;
    }

    public boolean isFalse(byte b) {
        return b == FALSE;
    }

    public boolean isTrue(byte b) {
        return b == TRUE;
    }
}
