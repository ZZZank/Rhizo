package dev.latvian.mods.rhino.util.unit;

public class SqrtUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("sqrt", Unit::sqrt);

	public SqrtUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.sqrt(a.get(variables));
	}
}
