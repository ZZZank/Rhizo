package dev.latvian.mods.rhino.util.unit;

public class DivUnit extends OpUnit {
	public DivUnit(Unit left, Unit right) {
		super(UnitSymbol.DIV, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return left.get(variables) / right.get(variables);
	}
}
