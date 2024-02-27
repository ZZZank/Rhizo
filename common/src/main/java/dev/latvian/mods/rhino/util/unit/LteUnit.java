package dev.latvian.mods.rhino.util.unit;

public class LteUnit extends CondOpUnit {
	public LteUnit(Unit left, Unit right) {
		super(UnitSymbol.LTE, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left.get(variables) <= right.get(variables);
	}
}
