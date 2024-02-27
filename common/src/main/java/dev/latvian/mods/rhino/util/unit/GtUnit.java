package dev.latvian.mods.rhino.util.unit;

public class GtUnit extends CondOpUnit {
	public GtUnit(Unit left, Unit right) {
		super(UnitSymbol.GT, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left.get(variables) > right.get(variables);
	}
}
