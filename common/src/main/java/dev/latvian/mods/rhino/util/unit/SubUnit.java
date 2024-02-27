package dev.latvian.mods.rhino.util.unit;

public class SubUnit extends OpUnit {
	public SubUnit(Unit left, Unit right) {
		super(UnitSymbol.SUB, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return left.get(variables) - right.get(variables);
	}
}
