package dev.latvian.mods.rhino.util.unit.operator.cond;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;
import dev.latvian.mods.rhino.util.unit.UnitVariables;

public class EqOpUnit extends CondOpUnit {
	public EqOpUnit(Unit left, Unit right) {
		super(UnitSymbol.EQ, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left == right || Math.abs(left.get(variables) - right.get(variables)) < 0.00001D;
	}
}
