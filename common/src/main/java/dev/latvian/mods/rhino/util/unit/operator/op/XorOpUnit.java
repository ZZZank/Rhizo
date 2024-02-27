package dev.latvian.mods.rhino.util.unit.operator.op;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;
import dev.latvian.mods.rhino.util.unit.UnitVariables;
import dev.latvian.mods.rhino.util.unit.operator.OpUnit;

public class XorOpUnit extends OpUnit {
	public XorOpUnit(Unit left, Unit right) {
		super(UnitSymbol.XOR, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return getInt(variables);
	}

	@Override
	public int getInt(UnitVariables variables) {
		return left.getInt(variables) ^ right.getInt(variables);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left.getBoolean(variables) ^ right.getBoolean(variables);
	}
}
