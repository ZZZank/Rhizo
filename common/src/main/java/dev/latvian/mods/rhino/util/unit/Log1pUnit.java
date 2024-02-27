package dev.latvian.mods.rhino.util.unit;

public class Log1pUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("log1p", Unit::log1p);

	public Log1pUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.log1p(a.get(variables));
	}
}
