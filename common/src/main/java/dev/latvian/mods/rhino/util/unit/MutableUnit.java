package dev.latvian.mods.rhino.util.unit;

/**
 * Mutable number
 */
public final class MutableUnit extends Unit {
	public double value;

	public MutableUnit(double value) {
		this.value = value;
	}

	public MutableUnit(float value) {
		this.value = value;
	}

	public void set(double value) {
		this.value = value;
	}

	public void set(float value) {
		this.value = value;
	}

	@Override
	public double get(UnitVariables variables) {
		return value;
	}

	@Override
	public void append(StringBuilder builder) {
		long r = Math.round(value);

		if (Math.abs(r - value) < 0.00001D) {
			builder.append(r);
		} else {
			builder.append(value);
		}
	}
}
