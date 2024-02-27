package dev.latvian.mods.rhino.util.unit.operator.op;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;
import dev.latvian.mods.rhino.util.unit.UnitVariables;
import dev.latvian.mods.rhino.util.unit.operator.OpUnit;

public class PowUnit extends OpUnit {
	public PowUnit(Unit left, Unit right) {
		super(UnitSymbol.POW, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.pow(left.get(variables), right.get(variables));
	}
}
