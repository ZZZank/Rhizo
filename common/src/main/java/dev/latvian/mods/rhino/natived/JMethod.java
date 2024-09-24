package dev.latvian.mods.rhino.natived;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.natived.original.JavaMembers;

import java.lang.reflect.Method;

/**
 * @author ZZZank
 */
public class JMethod {
    public final JavaMembers.MethodSignature signature;
    /**
     * for native name, use the name from {@link JavaMembers.MethodSignature}
     */
    public final String remappedName;
    public final int index;

    public final Class<?> returnType;

    public JMethod(Method m, Class<?> from, int index, Context cx) {
        this.remappedName = cx.getRemapper().remapMethod(from, m);
        this.index = index;
        this.signature = new JavaMembers.MethodSignature(m);
        this.returnType = m.getReturnType();
    }
}
