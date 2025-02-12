package dev.latvian.mods.rhino.test.builder;

import dev.latvian.mods.rhino.ClassShutter;
import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;

import java.util.function.BiConsumer;

/**
 * @author ZZZank
 */
public interface ContextModifier extends BiConsumer<ContextFactory, Context> {

    static ContextModifier languageVersion(int version) {
        return (factory, context) -> context.setLanguageVersion(version);
    }

    static ContextModifier optimizationLevel(int level) {
        return (factory, context) -> context.setOptimizationLevel(level);
    }

    static ContextModifier classShutter(ClassShutter classShutter) {
        return (factory, context) -> context.setClassShutter(classShutter);
    }
}
