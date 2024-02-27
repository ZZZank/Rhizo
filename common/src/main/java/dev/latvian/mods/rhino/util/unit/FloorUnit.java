package dev.latvian.mods.rhino.util.unit;

public class FloorUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("floor", Unit::floor);

	public FloorUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.floor(a.get(variables));
	}
}
