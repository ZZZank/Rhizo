package dev.latvian.mods.rhino.util.unit;

public class CeilUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("ceil", Unit::ceil);

	public CeilUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.ceil(a.get(variables));
	}
}
