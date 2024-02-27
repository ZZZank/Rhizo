package dev.latvian.mods.rhino.util.unit;

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