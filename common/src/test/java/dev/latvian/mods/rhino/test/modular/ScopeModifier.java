package dev.latvian.mods.rhino.test.modular;

import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;

/**
 * @author ZZZank
 */
public interface ScopeModifier extends Snapshot.Modifier {

    static ScopeModifier none() {
        return scope -> {};
    }

    static ScopeModifier putProperty(String name, Object value) {
        return scope -> ScriptableObject.putProperty(scope, name, value);
    }

    static ScopeModifier putConstProperty(String name, Object value) {
        return scope -> ScriptableObject.putConstProperty(scope, name, value);
    }

    static ScopeModifier importClass(String name, Class<?> clazz) {
        return scope -> ScriptableObject.putProperty(scope, name, new NativeJavaClass(scope, clazz));
    }

    static ScopeModifier importClass(Class<?> clazz) {
        return importClass(clazz.getSimpleName(), clazz);
    }

    void modify(Scriptable scope);

    @Override
    default void modify(Snapshot snapshot) {
        modify(snapshot.scope());
    }
}
