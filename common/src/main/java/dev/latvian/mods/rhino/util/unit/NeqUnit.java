package dev.latvian.mods.rhino.util.unit;

public class NeqUnit extends CondOpUnit {
	public NeqUnit(Unit left, Unit right) {
		super(UnitSymbol.NEQ, left, right);
	}

	@Override
	public boolean getBoolean(UnitVariables variables) {
		return left != right && Math.abs(left.get(variables) - right.get(variables)) >= 0.00001D;
	}
}
