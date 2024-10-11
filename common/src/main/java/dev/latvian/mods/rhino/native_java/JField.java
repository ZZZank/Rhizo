package dev.latvian.mods.rhino.native_java;

import dev.latvian.mods.rhino.Context;
import lombok.AllArgsConstructor;

import java.lang.reflect.Field;

/**
 * @author ZZZank
 */
@AllArgsConstructor
public class JField {
    public final String nativeName;
    public final String remappedName;
    public final int index;

    public final Class<?> type;

    public JField(Field f, Class<?> from, int index, Context cx) {
        nativeName = f.getName();
        remappedName = cx.getRemapper().remapField(from, f);
        this.index = index;
        type = f.getType();
    }
}
