package dev.latvian.mods.rhino.util.unit.operator;

import dev.latvian.mods.rhino.util.unit.Unit;

@FunctionalInterface
public interface UnaryOperatorFactory {
	Unit create(Unit unit);
}
