package dev.latvian.mods.rhino.util.unit;

@FunctionalInterface
public interface UnaryOperatorFactory {
	Unit create(Unit unit);
}
