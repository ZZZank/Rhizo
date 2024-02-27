package dev.latvian.mods.rhino.util.unit.operator.op;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;
import dev.latvian.mods.rhino.util.unit.UnitVariables;
import dev.latvian.mods.rhino.util.unit.operator.OpUnit;

public class DivOpUnit extends OpUnit {
	public DivOpUnit(Unit left, Unit right) {
		super(UnitSymbol.DIV, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return left.get(variables) / right.get(variables);
	}
}
