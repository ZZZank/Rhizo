package dev.latvian.mods.rhino.util.unit;

public class XorUnit extends OpUnit {
	public XorUnit(Unit left, Unit right) {
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
