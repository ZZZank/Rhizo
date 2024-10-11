package dev.latvian.mods.rhino.native_java;

import dev.latvian.mods.rhino.*;
import dev.latvian.mods.rhino.native_java.reflectasm.FieldAccess;
import dev.latvian.mods.rhino.native_java.reflectasm.MethodAccess;
import lombok.val;

import java.util.Map;
import java.util.function.Supplier;

/**
 * @author ZZZank
 */
public class JMembers extends ScriptableObject {

    public static JMembers lookup(Context cx, Scriptable scope, Class<?> dynamicType, Class<?> staticType) {
        val cache = (Map<Class<?>, JMembers>) (Object) cx.classTable;

        Class<?> c = dynamicType;
        JMembers members;
        while (true) {
            members = cache.get(c);
            if (members != null) {
                if (c != dynamicType) {
                    // member lookup for the original class failed because of missing privileges, cache the result so we don't try again
                    cache.put(dynamicType, members);
                }
                return members;
            }
            try {
                members = new JMembers(c, cx, scope);
                break;
            } catch (SecurityException e) {
                // Reflection may fail for objects that are in a restricted
                // access package (e.g. sun.*).  If we get a security
                // exception, try again with the static type if it is interface.
                // Otherwise, try superclass
                if (staticType != null && staticType.isInterface()) {
                    c = staticType;
                    staticType = null; // try staticType only once
                } else if (c != null) {
                    Class<?> parent = c.getSuperclass();
                    if (parent == null) {
                        if (c.isInterface()) {
                            // last resort after failed staticType interface
                            parent = ScriptRuntime.ObjectClass;
                        } else {
                            throw e;
                        }
                    }
                    c = parent;
                }
            }
        }

        cache.put(c, members);
        if (c != dynamicType) {
            // member lookup for the original class failed because of missing privileges, cache the result, so we don't try again
            cache.put(dynamicType, members);
        }
        return members;
    }

    public final Class<?> raw;
    private MethodAccess methodAccess;
    private FieldAccess fieldAccess;

    public JMembers(Class<?> c, Context cx, Scriptable scope) {
        val shutter = cx.getClassShutter();
        if (shutter != null && !shutter.visibleToScripts(c.getName(), ClassShutter.TYPE_MEMBER)) {
            throw Context.reportRuntimeError1("msg.access.prohibited", c.getName());
        }

        this.raw = c;
        this.methodAccess = tryOrDefault(() -> MethodAccess.get(raw), null);
        this.fieldAccess = tryOrDefault(() -> FieldAccess.get(raw), null);
    }

    @Override
    public String getClassName() {
        return "JavaMember";
    }

    private static <T> T tryOrDefault(Supplier<T> supplier, T defaultValue) {
        try {
            return supplier.get();
        } catch (Throwable t) {
            return defaultValue;
        }
    }
}
