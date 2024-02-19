package dev.latvian.mods.rhino.util;

import java.util.Collections;

public abstract class BackportUtil {
    
    public static String repeat(String str, int times) {
        return String.join("", Collections.nCopies(times, str));
    }
}
