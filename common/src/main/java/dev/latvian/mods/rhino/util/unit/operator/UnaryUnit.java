package dev.latvian.mods.rhino.util.unit.operator;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;

public abstract class UnaryUnit extends Unit {
	public final UnitSymbol symbol;
	public Unit unit;

	public UnaryUnit(UnitSymbol symbol, Unit unit) {
		this.symbol = symbol;
		this.unit = unit;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append('(');
		builder.append(symbol);
		unit.append(builder);
		builder.append(')');
	}
}