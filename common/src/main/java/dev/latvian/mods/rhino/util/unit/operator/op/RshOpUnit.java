package dev.latvian.mods.rhino.util.unit.operator.op;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitVariables;
import dev.latvian.mods.rhino.util.unit.operator.OpUnit;
import dev.latvian.mods.rhino.util.unit.token.UnitSymbol;

public class RshOpUnit extends OpUnit {
	public RshOpUnit(Unit left, Unit right) {
		super(UnitSymbol.RSH, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return getInt(variables);
	}

	@Override
	public int getInt(UnitVariables variables) {
		return left.getInt(variables) >> right.getInt(variables);
	}
}
