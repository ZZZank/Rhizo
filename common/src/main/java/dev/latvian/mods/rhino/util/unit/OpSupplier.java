package dev.latvian.mods.rhino.util.unit;

@FunctionalInterface
public interface OpSupplier {
	Unit create(Unit left, Unit right);
}
