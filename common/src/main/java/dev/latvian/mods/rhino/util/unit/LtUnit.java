package dev.latvian.mods.rhino.util.unit;

public class LtUnit extends CondOpUnit {
	public LtUnit(Unit left, Unit right) {
		super(UnitSymbol.LT, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left.get(variables) < right.get(variables);
	}
}
