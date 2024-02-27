package dev.latvian.mods.rhino.util.unit;

public class ShiftLeftUnit extends OpUnit {
	public ShiftLeftUnit(Unit left, Unit right) {
		super(UnitSymbol.LSH, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return getInt(variables);
	}

	@Override
	public int getInt(UnitVariables variables) {
		return left.getInt(variables) << right.getInt(variables);
	}
}
