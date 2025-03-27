package dev.latvian.mods.rhino.test.modular;

import dev.latvian.mods.rhino.ContextFactory;

/**
 * @author ZZZank
 */
public interface ContextFactoryModifier extends Snapshot.Modifier {

    static ContextFactoryModifier none() {
        return factory -> {};
    }

    static ContextFactoryModifier addListener(ContextFactory.Listener listener) {
        return factory -> factory.addListener(listener);
    }

    static ContextFactoryModifier removeListener(ContextFactory.Listener listener) {
        return factory -> factory.removeListener(listener);
    }

    static ContextFactoryModifier seal() {
        return ContextFactory::seal;
    }

    void modify(ContextFactory factory);

    @Override
    default void modify(Snapshot snapshot) {
        modify(snapshot.factory());
    }
}
