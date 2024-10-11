package dev.latvian.mods.rhino.native_java;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.native_java.original.MethodSignature;

import java.lang.reflect.Method;

/**
 * @author ZZZank
 */
public class JMethod {
    public final MethodSignature signature;
    /**
     * not for native name. use {@link MethodSignature#name()} for raw name in java runtime
     */
    public final String remappedName;
    public final int index;

    public final Class<?> returnType;

    public JMethod(Method m, Class<?> from, int index, Context cx) {
        this.remappedName = cx.getRemapper().remapMethod(from, m);
        this.index = index;
        this.signature = new MethodSignature(m);
        this.returnType = m.getReturnType();
    }
}
