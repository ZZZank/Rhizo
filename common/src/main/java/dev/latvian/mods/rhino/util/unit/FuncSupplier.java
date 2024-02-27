package dev.latvian.mods.rhino.util.unit;

@FunctionalInterface
public interface FuncSupplier {
	Unit create(Unit[] args);

	@FunctionalInterface interface Func1 extends FuncSupplier {
		Unit createArg(Unit a);
	
		/**
		 * only takes first one arg in {@code args}
		 */
		@Override
		default Unit create(Unit[] args) {
			return createArg(args[0]);
		}
	}

	@FunctionalInterface interface Func2 extends FuncSupplier {
		Unit createArg(Unit a, Unit b);
	
		/**
		 * only takes first two args in {@code args}
		 */
		@Override
		default Unit create(Unit[] args) {
			return createArg(args[0], args[1]);
		}
	}

	@FunctionalInterface interface Func3 extends FuncSupplier {
		Unit createArg(Unit a, Unit b, Unit c);
	
		/**
		 * only takes first three args in {@code args}
		 */
		@Override
		default Unit create(Unit[] args) {
			return createArg(args[0], args[1], args[2]);
		}
	}

}