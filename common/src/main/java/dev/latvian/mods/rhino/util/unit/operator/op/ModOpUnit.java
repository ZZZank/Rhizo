package dev.latvian.mods.rhino.util.unit.operator.op;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;
import dev.latvian.mods.rhino.util.unit.UnitVariables;
import dev.latvian.mods.rhino.util.unit.operator.OpUnit;

public class ModOpUnit extends OpUnit {
	public ModOpUnit(Unit left, Unit right) {
		super(UnitSymbol.MOD, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return left.get(variables) % right.get(variables);
	}
}
