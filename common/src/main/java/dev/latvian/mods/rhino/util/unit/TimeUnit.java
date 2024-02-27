package dev.latvian.mods.rhino.util.unit;

public class TimeUnit extends FuncUnit {
	public static final FuncSupplier SUPPLIER = new FuncSupplier() {
		@Override
		public Unit create(Unit[] args) {
			return new TimeUnit();
		}
	};

	public static double time() {
		return System.currentTimeMillis() / 1000D;
	}

	public static final FunctionFactory FACTORY = FunctionFactory.of0("time", TimeUnit::new);

	private TimeUnit() {
		super(FACTORY);
	}

	@Override
	public double get(UnitVariables variables) {
		return time();
	}


}