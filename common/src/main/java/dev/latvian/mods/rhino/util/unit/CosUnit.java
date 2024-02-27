package dev.latvian.mods.rhino.util.unit;

public class CosUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("cos", Unit::cos);

	public CosUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.cos(a.get(variables));
	}
}
