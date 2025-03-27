package dev.latvian.mods.rhino.test.modular;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.NativeJavaClass;
import dev.latvian.mods.rhino.Scriptable;
import dev.latvian.mods.rhino.ScriptableObject;

/**
 * @author ZZZank
 */
public interface ScopeModifier extends Snapshot.Modifier {

    static ScopeModifier none() {
        return (cx, scope) -> {};
    }

    static ScopeModifier putProperty(String name, Object value) {
        return (cx, scope) ->
            ScriptableObject.putProperty(scope, name, Context.javaToJS(cx, value, scope));
    }

    static ScopeModifier putConstProperty(String name, Object value) {
        return (cx, scope) ->
            ScriptableObject.putConstProperty(scope, name, Context.javaToJS(cx, value, scope));
    }

    static ScopeModifier importClass(String name, Class<?> clazz) {
        return (cx, scope) ->
            ScriptableObject.putProperty(scope, name, new NativeJavaClass(cx, scope, clazz));
    }

    static ScopeModifier importClass(Class<?> clazz) {
        return importClass(clazz.getSimpleName(), clazz);
    }

    void modify(Context cx, Scriptable scope);

    @Override
    default void modify(Snapshot snapshot) {
        modify(snapshot.context(), snapshot.scope());
    }
}
