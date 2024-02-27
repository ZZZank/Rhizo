package dev.latvian.mods.rhino.util.unit;

public class MaxUnit extends Func2Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of2("max", Unit::max);

	public MaxUnit(Unit a, Unit b) {
		super(FACTORY, a, b);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.max(a.get(variables), b.get(variables));
	}
}
