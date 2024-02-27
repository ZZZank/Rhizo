package dev.latvian.mods.rhino.util.unit;

public class AtanUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("atan", Unit::atan);

	public AtanUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.atan(a.get(variables));
	}
}
