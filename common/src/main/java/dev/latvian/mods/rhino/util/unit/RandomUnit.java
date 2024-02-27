package dev.latvian.mods.rhino.util.unit;

import java.util.Random;

public class RandomUnit extends FuncUnit {
	public static final Random RANDOM = new Random();
	public static final FuncSupplier SUPPLIER = new FuncSupplier() {
		@Override
		public Unit create(Unit[] args) {
			return new RandomUnit();
		}
	};

	private RandomUnit() {
		super(FACTORY);
	}

	public static final FunctionFactory FACTORY = FunctionFactory.of0("random", RandomUnit::new);

	@Override
	public double get(UnitVariables variables) {
		return RANDOM.nextDouble();
	}
}