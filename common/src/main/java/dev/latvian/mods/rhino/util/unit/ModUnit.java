package dev.latvian.mods.rhino.util.unit;

public class ModUnit extends OpUnit {
	public ModUnit(Unit left, Unit right) {
		super(UnitSymbol.MOD, left, right);
	}

	@Override
	public double get(UnitVariables variables) {
		return left.get(variables) % right.get(variables);
	}
}
