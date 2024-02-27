package dev.latvian.mods.rhino.util.unit;

public class Atan2Unit extends Func2Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of2("atan2", Unit::atan2);

	public Atan2Unit(Unit a, Unit b) {
		super(FACTORY, a, b);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.atan2(a.get(variables), b.get(variables));
	}
}
