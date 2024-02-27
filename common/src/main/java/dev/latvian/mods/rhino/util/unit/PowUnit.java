package dev.latvian.mods.rhino.util.unit;

public class PowUnit extends OpUnit {
	public PowUnit(Unit left, Unit right) {
		super(UnitSymbol.POW, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return Math.pow(left.get(variables), right.get(variables));
	}
}
