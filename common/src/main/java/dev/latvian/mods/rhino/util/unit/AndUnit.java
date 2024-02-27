package dev.latvian.mods.rhino.util.unit;

public class AndUnit extends OpUnit {
	public AndUnit(Unit left, Unit right) {
		super(UnitSymbol.BIT_AND, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return getInt(variables);
	}

	@Override
	public int getInt(UnitVariables variables) {
		return left.getInt(variables) & right.getInt(variables);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left.getBoolean(variables) && right.getBoolean(variables);
	}
}
