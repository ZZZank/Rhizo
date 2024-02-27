package dev.latvian.mods.rhino.util.unit.function;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitVariables;

public class CeilFuncUnit extends Func1Unit {
	public static final FunctionFactory FACTORY = FunctionFactory.of1("ceil", Unit::ceil);

	public CeilFuncUnit(Unit a) {
		super(FACTORY, a);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.ceil(a.get(variables));
	}
}
