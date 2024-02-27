package dev.latvian.mods.rhino.util.unit.operator;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;
import dev.latvian.mods.rhino.util.unit.UnitVariables;
import dev.latvian.mods.rhino.util.unit.VariableUnit;

public class SetUnit extends OpUnit {
	public SetUnit(UnitSymbol symbol, Unit left, Unit right) {
		super(symbol, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		if (left instanceof VariableUnit) {
		    VariableUnit var = (VariableUnit) left;
			variables.getVariables().set(var.name, right.get(variables));
		}

		return right.get(variables);
	}

	@Override
	public int getInt(UnitVariables variables) {
		if (left instanceof VariableUnit) {
		    VariableUnit var = (VariableUnit) left;
			variables.getVariables().set(var.name, right.get(variables));
		}

		return right.getInt(variables);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		if (left instanceof VariableUnit) {
		    VariableUnit var = (VariableUnit) left;
			variables.getVariables().set(var.name, right.get(variables));
		}

		return right.getBoolean(variables);
	}
}
