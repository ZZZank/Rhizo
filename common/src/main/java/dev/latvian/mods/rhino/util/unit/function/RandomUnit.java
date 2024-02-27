package dev.latvian.mods.rhino.util.unit.function;

import java.util.Random;

import dev.latvian.mods.rhino.util.unit.UnitVariables;

public class RandomUnit extends FuncUnit {
	public static final Random RANDOM = new Random();

	private RandomUnit() {
		super(FACTORY);
	}

	public static final FunctionFactory FACTORY = FunctionFactory.of0("random", RandomUnit::new);

	@Override
	public double get(UnitVariables variables) {
		return RANDOM.nextDouble();
	}


}