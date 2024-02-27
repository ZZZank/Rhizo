package dev.latvian.mods.rhino.util.unit;

public class MinUnit extends Func2Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of2("min", Unit::min);

	public MinUnit(Unit a, Unit b) {
		super(FACTORY, a, b);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.min(a.get(variables), b.get(variables));
	}
}
