package dev.latvian.mods.rhino.util.unit;

/**
 * @author ZZZank
 */
public class FixedBoolUnit extends FixedUnit {
    public static final FixedBoolUnit TRUE = new FixedBoolUnit(true);
    public static final FixedBoolUnit FALSE = new FixedBoolUnit(false);

    private final boolean b;

    public static FixedBoolUnit of(boolean b) {
        return b ? TRUE : FALSE;
    }

    private FixedBoolUnit(boolean b) {
        super(b ? 1F : 0F);
        this.b = b;
    }

    @Override
    public float get() {
        return b ? 1F : 0F;
    }

    @Override
    public boolean getAsBoolean() {
        return this.b;
    }

    @Override
    public int getAsInt() {
        return b ? 1 : 0;
    }

    @Override
    public Unit toBool() {
        return this;
    }

    @Override
    public void append(StringBuilder sb) {
        sb.append(b);
    }
}
