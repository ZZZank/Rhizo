package dev.latvian.mods.rhino.util.unit;

public class RadUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("rad", Unit::rad);

	public RadUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.toRadians(a.get(variables));
	}
}
