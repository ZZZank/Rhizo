package dev.latvian.mods.rhino.util.unit;

public class LogUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("log", Unit::log);

	public LogUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.log(a.get(variables));
	}
}
