package dev.latvian.mods.rhino.util.unit.token;

import java.util.Objects;

import dev.latvian.mods.rhino.util.unit.Unit;

public class UnaryOpUnitToken implements UnitToken {

    private final UnitSymbol operator;
    private final UnitToken token;
    public UnaryOpUnitToken(UnitSymbol operator, UnitToken token) {
        this.operator = operator;
        this.token = token;
    }
    public UnitSymbol operator() {
        return this.operator;
    }
    public UnitToken token() {
        return this.token;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof UnaryOpUnitToken)) {
            return false;
        }
        UnaryOpUnitToken other = (UnaryOpUnitToken) obj;
        return
            this.operator == other.operator &&
            this.token == other.token;
    }
    @Override
    public int hashCode() {
        return Objects.hash(operator, token);
    }
	@Override
	public Unit interpret(UnitTokenStream stream) {
		Unit unit = token.interpret(stream);
		return operator.unaryOp.create(unit);
	}

	@Override
	public String toString() {
		return "(" + operator + token + ")";
	}
}
