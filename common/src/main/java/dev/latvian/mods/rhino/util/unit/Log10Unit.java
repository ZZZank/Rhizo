package dev.latvian.mods.rhino.util.unit;

public class Log10Unit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("log10", Unit::log10);

	public Log10Unit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.log10(a.get(variables));
	}
}
