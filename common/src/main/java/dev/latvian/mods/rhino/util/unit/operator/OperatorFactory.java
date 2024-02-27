package dev.latvian.mods.rhino.util.unit.operator;

import dev.latvian.mods.rhino.util.unit.Unit;

@FunctionalInterface
public interface OperatorFactory {
	Unit create(Unit left, Unit right);
}
