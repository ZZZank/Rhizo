package dev.latvian.mods.rhino.util.unit;

import org.jetbrains.annotations.Nullable;

public class EmptyVariableSet extends VariableSet {

	public static final EmptyVariableSet INSTANCE = new EmptyVariableSet();

	private EmptyVariableSet() {
	}

	@Override
	public VariableSet set(String name, Unit value) {
		return this;
	}

	@Override
	public VariableSet set(String name, double value) {
		return this;
	}

	@Override
	public MutableUnit setMutable(String name, double initialValue) {
		return new MutableUnit(initialValue);
	}

	@Override
	@Nullable
	public Unit get(String entry) {
		return null;
	}

	@Override
	public VariableSet createSubset() {
		return new VariableSet();
	}
}
