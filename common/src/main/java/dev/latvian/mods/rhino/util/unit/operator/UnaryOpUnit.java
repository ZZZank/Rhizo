package dev.latvian.mods.rhino.util.unit.operator;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;

public abstract class UnaryOpUnit extends Unit {
	public final UnitSymbol symbol;
	public Unit unit;

	public UnaryOpUnit(UnitSymbol symbol, Unit unit) {
		this.symbol = symbol;
		this.unit = unit;
	}

	@Override
	public void toString(StringBuilder builder) {
		builder.append('(');
		builder.append(symbol);
		unit.toString(builder);
		builder.append(')');
	}
}