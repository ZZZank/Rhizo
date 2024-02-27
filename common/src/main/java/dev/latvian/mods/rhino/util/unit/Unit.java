package dev.latvian.mods.rhino.util.unit;

import dev.latvian.mods.rhino.util.unit.function.AbsFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.Atan2FuncUnit;
import dev.latvian.mods.rhino.util.unit.function.AtanFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.BoolFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.CeilFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.ClampFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.CosFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.DegFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.FloorFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.LerpFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.Log10FuncUnit;
import dev.latvian.mods.rhino.util.unit.function.Log1pFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.LogFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.MaxFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.MinFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.RadFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.SinFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.SmoothstepFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.SqFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.SqrtFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.TanFuncUnit;
import dev.latvian.mods.rhino.util.unit.function.WithAlphaFuncUnit;
import dev.latvian.mods.rhino.util.unit.operator.BiNotUnit;
import dev.latvian.mods.rhino.util.unit.operator.NotUnit;
import dev.latvian.mods.rhino.util.unit.operator.NegUnit;
import dev.latvian.mods.rhino.util.unit.operator.SetUnit;
import dev.latvian.mods.rhino.util.unit.operator.cond.AndOpUnit;
import dev.latvian.mods.rhino.util.unit.operator.cond.EqUnit;
import dev.latvian.mods.rhino.util.unit.operator.cond.GtUnit;
import dev.latvian.mods.rhino.util.unit.operator.cond.GteUnit;
import dev.latvian.mods.rhino.util.unit.operator.cond.LtUnit;
import dev.latvian.mods.rhino.util.unit.operator.cond.LteUnit;
import dev.latvian.mods.rhino.util.unit.operator.cond.NeqUnit;
import dev.latvian.mods.rhino.util.unit.operator.cond.OrUnit;
import dev.latvian.mods.rhino.util.unit.operator.op.AddUnit;
import dev.latvian.mods.rhino.util.unit.operator.op.AndUnit;
import dev.latvian.mods.rhino.util.unit.operator.op.OrUnit;
import dev.latvian.mods.rhino.util.unit.operator.op.DivUnit;
import dev.latvian.mods.rhino.util.unit.operator.op.LshUnit;
import dev.latvian.mods.rhino.util.unit.operator.op.ModUnit;
import dev.latvian.mods.rhino.util.unit.operator.op.MulUnit;
import dev.latvian.mods.rhino.util.unit.operator.op.PowUnit;
import dev.latvian.mods.rhino.util.unit.operator.op.RshUnit;
import dev.latvian.mods.rhino.util.unit.operator.op.SubUnit;
import dev.latvian.mods.rhino.util.unit.operator.op.XorUnit;

public abstract class Unit {
	public static Unit[] EMPTY_ARRAY = new Unit[0];

	public boolean isFixed() {
		return false;
	}

	public abstract double get(UnitVariables variables);

	public float getFloat(UnitVariables variables) {
		return (float) get(variables);
	}

	public int getInt(UnitVariables variables) {
		double d = get(variables);
		int i = (int) d;
		return d < (double) i ? i - 1 : i;
	}

	public boolean getBoolean(UnitVariables variables) {
		return get(variables) != 0D;
	}

	public void append(StringBuilder builder) {
		builder.append(this);
	}

	public String toString() {
		StringBuilder builder = new StringBuilder();
		append(builder);
		return builder.toString();
	}

	// Operators

	public Unit positive() {
		return this;
	}

	public Unit negate() {
		return new NegUnit(this);
	}

	public Unit add(Unit other) {
		return new AddUnit(this, other);
	}

	public Unit add(double value) {
		return add(FixedUnit.of(value));
	}

	public Unit sub(Unit other) {
		return new SubUnit(this, other);
	}

	public Unit sub(double value) {
		return sub(FixedUnit.of(value));
	}

	public Unit mul(Unit other) {
		return new MulUnit(this, other);
	}

	public Unit mul(double value) {
		return add(FixedUnit.of(value));
	}

	public Unit div(Unit other) {
		return new DivUnit(this, other);
	}

	public Unit div(double value) {
		return add(FixedUnit.of(value));
	}

	public Unit mod(Unit other) {
		return new ModUnit(this, other);
	}

	public Unit mod(double value) {
		return mod(FixedUnit.of(value));
	}

	public Unit pow(Unit other) {
		return new PowUnit(this, other);
	}

	public Unit lsh(Unit other) {
		return new LshUnit(this, other);
	}

	public Unit rsh(Unit other) {
		return new RshUnit(this, other);
	}

	public Unit bitAnd(Unit other) {
		return new AndUnit(this, other);
	}

	public Unit bitOr(Unit other) {
		return new OrUnit(this, other);
	}

	public Unit xor(Unit other) {
		return new XorUnit(this, other);
	}

	public Unit bitNot() {
		return new BiNotUnit(this);
	}

	public Unit eq(Unit other) {
		return new EqUnit(this, other);
	}

	public Unit neq(Unit other) {
		return new NeqUnit(this, other);
	}

	public Unit lt(Unit other) {
		return new LtUnit(this, other);
	}

	public Unit gt(Unit other) {
		return new GtUnit(this, other);
	}

	public Unit lte(Unit other) {
		return new LteUnit(this, other);
	}

	public Unit gte(Unit other) {
		return new GteUnit(this, other);
	}

	public Unit and(Unit other) {
		return new AndOpUnit(this, other);
	}

	public Unit or(Unit other) {
		return new OrUnit(this, other);
	}

	public Unit boolNot() {
		return new NotUnit(this);
	}

	// Functions

	public Unit min(Unit other) {
		return new MinFuncUnit(this, other);
	}

	public Unit max(Unit other) {
		return new MaxFuncUnit(this, other);
	}

	public Unit abs() {
		return new AbsFuncUnit(this);
	}

	public Unit sin() {
		return new SinFuncUnit(this);
	}

	public Unit cos() {
		return new CosFuncUnit(this);
	}

	public Unit tan() {
		return new TanFuncUnit(this);
	}

	public Unit deg() {
		return new DegFuncUnit(this);
	}

	public Unit rad() {
		return new RadFuncUnit(this);
	}

	public Unit atan() {
		return new AtanFuncUnit(this);
	}

	public Unit atan2(Unit other) {
		return new Atan2FuncUnit(this, other);
	}

	public Unit log() {
		return new LogFuncUnit(this);
	}

	public Unit log10() {
		return new Log10FuncUnit(this);
	}

	public Unit log1p() {
		return new Log1pFuncUnit(this);
	}

	public Unit sqrt() {
		return new SqrtFuncUnit(this);
	}

	public Unit sq() {
		return new SqFuncUnit(this);
	}

	public Unit floor() {
		return new FloorFuncUnit(this);
	}

	public Unit ceil() {
		return new CeilFuncUnit(this);
	}

	public Unit bool() {
		return new BoolFuncUnit(this);
	}

	public Unit clamp(Unit a, Unit b) {
		return new ClampFuncUnit(this, a, b);
	}

	public Unit lerp(Unit a, Unit b) {
		return new LerpFuncUnit(this, a, b);
	}

	public Unit smoothstep() {
		return new SmoothstepFuncUnit(this);
	}

	public Unit withAlpha(Unit a) {
		return new WithAlphaFuncUnit(this, a);
	}

	public Unit set(Unit unit) {
		return new SetUnit(UnitSymbol.SET, this, unit);
	}

	public Unit addSet(Unit unit) {
		return new SetUnit(UnitSymbol.ADD_SET, this, add(unit));
	}

	public Unit subSet(Unit unit) {
		return new SetUnit(UnitSymbol.SUB_SET, this, sub(unit));
	}

	public Unit mulSet(Unit unit) {
		return new SetUnit(UnitSymbol.MUL_SET, this, mul(unit));
	}

	public Unit divSet(Unit unit) {
		return new SetUnit(UnitSymbol.DIV_SET, this, div(unit));
	}

	public Unit modSet(Unit unit) {
		return new SetUnit(UnitSymbol.MOD_SET, this, mod(unit));
	}
}
