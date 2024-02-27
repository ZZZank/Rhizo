package dev.latvian.mods.rhino.util.unit.operator.cond;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitVariables;
import dev.latvian.mods.rhino.util.unit.token.UnitSymbol;

public class NeqOpUnit extends CondOpUnit {
	public NeqOpUnit(Unit left, Unit right) {
		super(UnitSymbol.NEQ, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left != right && Math.abs(left.get(variables) - right.get(variables)) >= 0.00001D;
	}
}
