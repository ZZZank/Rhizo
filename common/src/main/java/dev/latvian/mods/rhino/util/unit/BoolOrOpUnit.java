package dev.latvian.mods.rhino.util.unit;

public class BoolOrOpUnit extends CondOpUnit {
	public BoolOrOpUnit(Unit left, Unit right) {
		super(UnitSymbol.OR, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left.getBoolean(variables) || right.getBoolean(variables);
	}
}
