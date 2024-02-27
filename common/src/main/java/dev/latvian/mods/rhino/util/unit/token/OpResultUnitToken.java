package dev.latvian.mods.rhino.util.unit.token;

import java.util.Objects;

import dev.latvian.mods.rhino.util.unit.Unit;

public class OpResultUnitToken implements UnitToken {

    private final UnitSymbol operator;
    private final UnitToken left;
    private final UnitToken right;
    public OpResultUnitToken(UnitSymbol operator, UnitToken left, UnitToken right) {
        this.operator = operator;
        this.left = left;
        this.right = right;
    }
    public UnitSymbol operator() {
        return this.operator;
    }
    public UnitToken left() {
        return this.left;
    }
    public UnitToken right() {
        return this.right;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof OpResultUnitToken)) {
            return false;
        }
        OpResultUnitToken other = (OpResultUnitToken) obj;
        return
            this.operator == other.operator &&
            this.left == other.left &&
            this.right == other.right;
    }
    @Override
    public int hashCode() {
        return Objects.hash(operator, left, right);
    }
	@Override
	public Unit interpret(UnitTokenStream stream) {
		Unit uleft = left.interpret(stream);
		Unit uright = right.interpret(stream);
		return operator.op.create(uleft, uright);
	}

	@Override
	public String toString() {
		return "(" + left + " " + operator + " " + right + ")";
	}
}
