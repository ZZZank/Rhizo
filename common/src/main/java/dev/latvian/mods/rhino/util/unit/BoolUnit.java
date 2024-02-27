package dev.latvian.mods.rhino.util.unit;

public class BoolUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("bool", Unit::bool);

	public BoolUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return getBoolean(variables) ? 1D : 0D;
	}

	@Override
	public float getFloat(UnitVariables variables) {
		return getBoolean(variables) ? 1F : 0F;
	}

	@Override
	public int getInt(UnitVariables variables) {
		return getBoolean(variables) ? 1 : 0;
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return a.getBoolean(variables);
	}
}
