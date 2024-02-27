package dev.latvian.mods.rhino.util.unit;

public class AddUnit extends OpUnit {
	public AddUnit(Unit left, Unit right) {
		super(UnitSymbol.ADD, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return left.get(variables) + right.get(variables);
	}
}
