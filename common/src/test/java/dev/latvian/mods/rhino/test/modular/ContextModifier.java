package dev.latvian.mods.rhino.test.modular;

import dev.latvian.mods.rhino.ClassShutter;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.WrapFactory;

/**
 * @author ZZZank
 */
public interface ContextModifier extends Snapshot.Modifier {

    static ContextModifier none() {
        return context -> {};
    }

    static ContextModifier setLanguageVersion(int version) {
        return context -> context.setLanguageVersion(version);
    }

    static ContextModifier setOptimizationLevel(int optimizationLevel) {
        return context -> context.setOptimizationLevel(optimizationLevel);
    }

    static ContextModifier setClassShutter(ClassShutter classShutter) {
        return context -> context.setClassShutter(classShutter);
    }

    static ContextModifier setWrapFactory(WrapFactory wrapFactory) {
        return context -> context.setWrapFactory(wrapFactory);
    }

    void modify(Context context);

    @Override
    default void modify(Snapshot snapshot) {
        modify(snapshot.context());
    }
}
