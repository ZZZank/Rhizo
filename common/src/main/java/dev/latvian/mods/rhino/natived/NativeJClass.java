package dev.latvian.mods.rhino.natived;

import dev.latvian.mods.rhino.natived.original.JavaMembers;
import dev.latvian.mods.rhino.NativeJavaObject;
import dev.latvian.mods.rhino.Scriptable;

/**
 * @author ZZZank
 */
public class NativeJClass extends NativeJavaObject {

    public NativeJClass(Scriptable scope, Class<?> clazz) {
        this(scope, clazz, false);
    }

    public NativeJClass(Scriptable scope, Class<?> clazz, boolean isAdapter) {
        super(scope, clazz, null, isAdapter);
    }

    public Class<?> raw() {
        return (Class<?>) this.javaObject;
    }

    @Override
    protected void initMembers() {
        members = JavaMembers.lookupClass(parent, raw(), raw(), isAdapter);
    }

    @Override
    public String getClassName() {
        return "JavaClass";
    }
}
