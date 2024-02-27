package dev.latvian.mods.rhino.util.unit.operator;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.UnitSymbol;

public abstract class OpUnit extends Unit {
	public final UnitSymbol symbol;
	public Unit left;
	public Unit right;

	public OpUnit(UnitSymbol symbol, Unit left, Unit right) {
		this.symbol = symbol;
		this.left = left;
		this.right = right;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append('(');

		if (left == null) {
			builder.append("null");
		} else {
			left.append(builder);
		}

		builder.append(symbol);

		if (right == null) {
			builder.append("null");
		} else {
			right.append(builder);
		}

		builder.append(')');
	}
}
