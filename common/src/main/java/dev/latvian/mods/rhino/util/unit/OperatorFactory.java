package dev.latvian.mods.rhino.util.unit;

@FunctionalInterface
public interface OperatorFactory {
	Unit create(Unit left, Unit right);
}
