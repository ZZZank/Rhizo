package dev.latvian.mods.rhino.util.unit;

public class NegUnit extends UnaryUnit {
	public NegUnit(Unit unit) {
		super(UnitSymbol.BIT_NOT, unit);
	}

	@Override
	public double get(UnitVariables variables) {
		return -unit.get(variables);
	}

	@Override
	public int getInt(UnitVariables variables) {
		return -unit.getInt(variables);
	}
}
