package dev.latvian.mods.rhino.util.unit.operator.cond;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;
import dev.latvian.mods.rhino.util.unit.UnitVariables;

public class OrUnit extends CondOpUnit {
	public OrUnit(Unit left, Unit right) {
		super(UnitSymbol.OR, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left.getBoolean(variables) || right.getBoolean(variables);
	}
}
