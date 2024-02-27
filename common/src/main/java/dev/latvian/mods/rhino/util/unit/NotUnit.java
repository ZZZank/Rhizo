package dev.latvian.mods.rhino.util.unit;

public class NotUnit extends UnaryUnit {
	public NotUnit(Unit unit) {
		super(UnitSymbol.BOOL_NOT, unit);
	}

	@Override
	public double get(UnitVariables variables) {
		return getBoolean(variables) ? 1.0D : 0.0D;
	}

	@Override
	public int getInt(UnitVariables variables) {
		return getBoolean(variables) ? 1 : 0;
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return !unit.getBoolean(variables);
	}
}
