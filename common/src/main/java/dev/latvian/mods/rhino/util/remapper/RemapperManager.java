package dev.latvian.mods.rhino.util.remapper;

/**
 * @author ZZZank
 */
public abstract class RemapperManager {

    private static Remapper defaultRemapper = AnnotatedRemapper.INSTANCE;

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
