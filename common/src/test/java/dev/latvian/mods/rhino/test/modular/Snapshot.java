package dev.latvian.mods.rhino.test.modular;

import dev.latvian.mods.rhino.Context;
import dev.latvian.mods.rhino.ContextFactory;
import dev.latvian.mods.rhino.Scriptable;

import java.util.Collection;

/**
 * @author ZZZank
 */
public interface Snapshot {
    ContextFactory factory();

    Context context();

    Scriptable scope();

    Object result();

    Exception error();

    default boolean testFired() {
        return (result() == null) != (error() == null);
    }

    default boolean hasError() {
        return error() != null;
    }

    default void accept(Collection<? extends Modifier> modifiers) {
        for (final var modifier : modifiers) {
            modifier.modify(this);
        }
    }

    default void accept(Modifier... modifiers) {
        for (final var modifier : modifiers) {
            modifier.modify(this);
        }
    }

    @FunctionalInterface
    interface Modifier {
        void modify(Snapshot snapshot);
    }
}
