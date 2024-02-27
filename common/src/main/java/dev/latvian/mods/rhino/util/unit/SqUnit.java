package dev.latvian.mods.rhino.util.unit;

public class SqUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("sq", Unit::sq);

	public SqUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		double x = a.get(variables);
		return x * x;
	}
}
