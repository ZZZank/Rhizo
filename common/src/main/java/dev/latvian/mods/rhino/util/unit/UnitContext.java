package dev.latvian.mods.rhino.util.unit;

import dev.latvian.mods.rhino.util.BackportUtil;
import dev.latvian.mods.rhino.util.unit.function.AbsFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.Atan2FuncUnit;
import dev.latvian.mods.rhino.util.unit.function.AtanFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.BoolFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.CeilFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.ClampFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.CosFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.DegFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.FloorFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.FunctionFactory;
import dev.latvian.mods.rhino.util.unit.function.HsvFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.LerpFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.Log10FuncUnit;
import dev.latvian.mods.rhino.util.unit.function.Log1pFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.LogFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.MapFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.MaxFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.MinFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.RadFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.RandomUnit;
import dev.latvian.mods.rhino.util.unit.function.RgbFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.RoundedTimeUnit;
import dev.latvian.mods.rhino.util.unit.function.SinFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.SmoothstepFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.SqFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.SqrtFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.TanFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.TimeUnit;
import dev.latvian.mods.rhino.util.unit.function.WithAlphaFuncUnit;
import dev.latvian.mods.rhino.util.unit.token.UnitTokenStream;

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
		DEFAULT.addFunction(RgbFuncUnit.FACTORY);

		DEFAULT.addFunction(MinFuncUnit.FACTORY);
		DEFAULT.addFunction(MaxFuncUnit.FACTORY);
		DEFAULT.addFunction(AbsFuncUnit.FACTORY);
		DEFAULT.addFunction(SinFuncUnit.FACTORY);
		DEFAULT.addFunction(CosFuncUnit.FACTORY);
		DEFAULT.addFunction(TanFuncUnit.FACTORY);
		DEFAULT.addFunction(DegFuncUnit.FACTORY);
		DEFAULT.addFunction(RadFuncUnit.FACTORY);
		DEFAULT.addFunction(AtanFuncUnit.FACTORY);
		DEFAULT.addFunction(Atan2FuncUnit.FACTORY);
		DEFAULT.addFunction(LogFuncUnit.FACTORY);
		DEFAULT.addFunction(Log10FuncUnit.FACTORY);
		DEFAULT.addFunction(Log1pFuncUnit.FACTORY);
		DEFAULT.addFunction(SqrtFuncUnit.FACTORY);
		DEFAULT.addFunction(SqFuncUnit.FACTORY);
		DEFAULT.addFunction(FloorFuncUnit.FACTORY);
		DEFAULT.addFunction(CeilFuncUnit.FACTORY);
		DEFAULT.addFunction(BoolFuncUnit.FACTORY);
		DEFAULT.addFunction(ClampFuncUnit.FACTORY);
		DEFAULT.addFunction(LerpFuncUnit.FACTORY);
		DEFAULT.addFunction(SmoothstepFuncUnit.FACTORY);
		DEFAULT.addFunction(HsvFuncUnit.FACTORY);
		DEFAULT.addFunction(WithAlphaFuncUnit.FACTORY);
		DEFAULT.addFunction(MapFuncUnit.FACTORY);

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
