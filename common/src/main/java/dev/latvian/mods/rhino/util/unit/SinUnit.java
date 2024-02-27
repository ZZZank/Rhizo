package dev.latvian.mods.rhino.util.unit;

public class SinUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("sin", Unit::sin);

	public SinUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.sin(a.get(variables));
	}
}
