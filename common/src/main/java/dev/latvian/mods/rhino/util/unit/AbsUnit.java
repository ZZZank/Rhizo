package dev.latvian.mods.rhino.util.unit;

public class AbsUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("abs", Unit::abs);

	public AbsUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.abs(a.get(variables));
	}
}
