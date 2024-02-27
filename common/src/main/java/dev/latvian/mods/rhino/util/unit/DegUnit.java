package dev.latvian.mods.rhino.util.unit;

public class DegUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("deg", Unit::deg);

	public DegUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.toDegrees(a.get(variables));
	}
}
