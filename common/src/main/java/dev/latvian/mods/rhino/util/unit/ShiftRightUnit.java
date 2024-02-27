package dev.latvian.mods.rhino.util.unit;

public class ShiftRightUnit extends OpUnit {
	public ShiftRightUnit(Unit left, Unit right) {
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
