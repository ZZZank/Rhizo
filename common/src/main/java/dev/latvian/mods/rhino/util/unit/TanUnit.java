package dev.latvian.mods.rhino.util.unit;

public class TanUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("tan", Unit::tan);

	public TanUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.tan(a.get(variables));
	}
}
