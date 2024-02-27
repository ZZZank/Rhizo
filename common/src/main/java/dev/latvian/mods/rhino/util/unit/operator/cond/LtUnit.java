package dev.latvian.mods.rhino.util.unit.operator.cond;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;
import dev.latvian.mods.rhino.util.unit.UnitVariables;

public class LtUnit extends CondOpUnit {
	public LtUnit(Unit left, Unit right) {
		super(UnitSymbol.LT, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left.get(variables) < right.get(variables);
	}
}
