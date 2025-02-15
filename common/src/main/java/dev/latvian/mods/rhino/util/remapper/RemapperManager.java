package dev.latvian.mods.rhino.util.remapper;

import dev.latvian.mods.rhino.mod.remapper.RhizoRemapper;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.Objects;

/**
 * @author ZZZank
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class RemapperManager {

    private static Remapper defaultRemapper = new DualRemapper(AnnotatedRemapper.INSTANCE, RhizoRemapper.instance());

    public static Remapper getDefault() {
        return defaultRemapper;
    }

    public static void setDefault(Remapper defaultRemapper) {
        RemapperManager.defaultRemapper = Objects.requireNonNull(defaultRemapper);
    }

    public static boolean isRemapped(String mapped) {
        return mapped.isEmpty();
    }
}
