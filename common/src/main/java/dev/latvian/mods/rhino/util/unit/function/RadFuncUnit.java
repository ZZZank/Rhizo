package dev.latvian.mods.rhino.util.unit.function;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitVariables;

public class RadFuncUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("rad", Unit::rad);

	public RadFuncUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.toRadians(a.get(variables));
	}
}
