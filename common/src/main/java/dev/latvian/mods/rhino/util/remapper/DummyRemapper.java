package dev.latvian.mods.rhino.util.remapper;

/**
 * only returns empty string
 */
public class DummyRemapper implements Remapper {

    public static final DummyRemapper INSTANCE = new DummyRemapper();

    private DummyRemapper() {}
}
