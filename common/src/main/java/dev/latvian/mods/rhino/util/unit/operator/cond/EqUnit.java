package dev.latvian.mods.rhino.util.unit.operator.cond;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;
import dev.latvian.mods.rhino.util.unit.UnitVariables;

public class EqUnit extends CondOpUnit {
	public EqUnit(Unit left, Unit right) {
		super(UnitSymbol.EQ, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left == right || Math.abs(left.get(variables) - right.get(variables)) < 0.00001D;
	}
}
