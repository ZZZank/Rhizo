package dev.latvian.mods.rhino.util.unit;

import dev.latvian.mods.rhino.util.BackportUtil;

import org.jetbrains.annotations.Nullable;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.stream.Collectors;

public class UnitContext {
	public static final UnitContext DEFAULT = new UnitContext();

	static {
		DEFAULT.addFunction(TimeUnit.FACTORY);
		DEFAULT.addFunction(RoundedTimeUnit.FACTORY);
		DEFAULT.addFunction(RandomUnit.FACTORY);
		DEFAULT.addFunction(FunctionFactory.of3("if", TernaryUnit::new));
		DEFAULT.addFunction(ColorUnit.FACTORY);

		DEFAULT.addFunction(MinUnit.FACTORY);
		DEFAULT.addFunction(MaxUnit.FACTORY);
		DEFAULT.addFunction(AbsUnit.FACTORY);
		DEFAULT.addFunction(SinUnit.FACTORY);
		DEFAULT.addFunction(CosUnit.FACTORY);
		DEFAULT.addFunction(TanUnit.FACTORY);
		DEFAULT.addFunction(DegUnit.FACTORY);
		DEFAULT.addFunction(RadUnit.FACTORY);
		DEFAULT.addFunction(AtanUnit.FACTORY);
		DEFAULT.addFunction(Atan2Unit.FACTORY);
		DEFAULT.addFunction(LogUnit.FACTORY);
		DEFAULT.addFunction(Log10Unit.FACTORY);
		DEFAULT.addFunction(Log1pUnit.FACTORY);
		DEFAULT.addFunction(SqrtUnit.FACTORY);
		DEFAULT.addFunction(SqUnit.FACTORY);
		DEFAULT.addFunction(FloorUnit.FACTORY);
		DEFAULT.addFunction(CeilUnit.FACTORY);
		DEFAULT.addFunction(BoolUnit.FACTORY);
		DEFAULT.addFunction(ClampFuncUnit.FACTORY);
		DEFAULT.addFunction(LerpFuncUnit.FACTORY);
		DEFAULT.addFunction(SmoothstepFuncUnit.FACTORY);
		DEFAULT.addFunction(HsvFuncUnit.FACTORY);
		DEFAULT.addFunction(WithAlphaFuncUnit.FACTORY);
		DEFAULT.addFunction(MapUnit.FACTORY);

		DEFAULT.addConstant("true", FixedBooleanUnit.TRUE);
		DEFAULT.addConstant("false", FixedBooleanUnit.FALSE);
		DEFAULT.addConstant("PI", FixedUnit.PI);
		DEFAULT.addConstant("TWO_PI", FixedUnit.TWO_PI);
		DEFAULT.addConstant("HALF_PI", FixedUnit.HALF_PI);
		DEFAULT.addConstant("E", FixedUnit.E);
	}

	public final Map<String, Unit> constants = new HashMap<>();
	private final Map<String, FunctionFactory> functions = new HashMap<>();
	private final Map<String, Unit> cache = new HashMap<>();
	private int debug = -1;

	public void addFunction(FunctionFactory factory) {
		functions.put(factory.name(), factory);
	}

	@Nullable
	public FunctionFactory getFunctionFactory(String name) {
		return functions.get(name);
	}

	public void addConstant(String s, Unit u) {
		constants.put(s, u);
	}

	public UnitContext sub() {
		UnitContext ctx = new UnitContext();
		ctx.functions.putAll(functions);
		ctx.debug = debug;
		return ctx;
	}

	public UnitTokenStream createStream(String input) {
		return new UnitTokenStream(this, input);
	}

	public Unit parse(String input) {
		Unit u = cache.get(input);

		if (u == null) {
			u = createStream(input).getUnit();
			cache.put(input, u);
		}

		return u;
	}

	public boolean isDebug() {
		return debug >= 0;
	}

	public void pushDebug() {
		debug++;
	}

	public void popDebug() {
		debug--;
	}

	public void debugInfo(String s) {
		if (debug >= 0) {
			if (debug >= 2) {
				System.out.println(BackportUtil.repeat("  ", debug-1) + s);
			} else {
				System.out.println(s);
			}
		}
	}

	public void debugInfo(String s, Collection<?> values) {
		debugInfo(s + ": " + values.stream().map(Object::toString).collect(Collectors.joining("  ")));
	}
}
