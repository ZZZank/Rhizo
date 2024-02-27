package dev.latvian.mods.rhino.util.unit.operator;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;
import dev.latvian.mods.rhino.util.unit.UnitVariables;

public class BitNotOpUnit extends UnaryOpUnit {
	public BitNotOpUnit(Unit unit) {
		super(UnitSymbol.BIT_NOT, unit);
	}

	@Override
	public double get(UnitVariables variables) {
		return getInt(variables);
	}

	@Override
	public int getInt(UnitVariables variables) {
		return ~unit.getInt(variables);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return !unit.getBoolean(variables);
	}
}
