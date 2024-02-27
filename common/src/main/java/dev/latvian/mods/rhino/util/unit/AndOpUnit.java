package dev.latvian.mods.rhino.util.unit;

public class AndOpUnit extends CondOpUnit {
	public AndOpUnit(Unit left, Unit right) {
		super(UnitSymbol.AND, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left.getBoolean(variables) && right.getBoolean(variables);
	}
}
