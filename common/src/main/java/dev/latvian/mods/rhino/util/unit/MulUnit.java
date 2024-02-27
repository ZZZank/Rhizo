package dev.latvian.mods.rhino.util.unit;

public class MulUnit extends OpUnit {
	public MulUnit(Unit left, Unit right) {
		super(UnitSymbol.MUL, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return left.get(variables) * right.get(variables);
	}
}
