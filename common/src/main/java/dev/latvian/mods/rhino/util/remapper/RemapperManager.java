package dev.latvian.mods.rhino.util.remapper;

import dev.latvian.mods.rhino.mod.remapper.RhizoRemapper;

/**
 * @author ZZZank
 */
public abstract class RemapperManager {

    private static Remapper defaultRemapper = new DualRemapper(AnnotatedRemapper.INSTANCE, RhizoRemapper.instance());

    public static Remapper getDefault() {
        return defaultRemapper;
    }

    public static void setDefault(Remapper defaultRemapper) {
        RemapperManager.defaultRemapper = defaultRemapper;
    }

    public static boolean isRemapped(String mapped) {
        return mapped.isEmpty();
    }
}
