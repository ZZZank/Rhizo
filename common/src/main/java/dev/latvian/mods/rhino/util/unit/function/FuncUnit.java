package dev.latvian.mods.rhino.util.unit.function;

import dev.latvian.mods.rhino.util.unit.Unit;

public abstract class FuncUnit extends Unit {
	private static final Unit[] NO_ARGS = new Unit[0];

	public final FunctionFactory factory;

	public FuncUnit(FunctionFactory factory) {
		this.factory = factory;
	}

	protected Unit[] getArguments() {
		return NO_ARGS;
	}

	@Override
	public void append(StringBuilder builder) {
		builder.append(factory.name());
		builder.append('(');

		Unit[] args = getArguments();

		for (int i = 0; i < args.length; i++) {
			if (i > 0) {
				builder.append(',');
			}

			args[i].append(builder);
		}

		builder.append(')');
	}
}
