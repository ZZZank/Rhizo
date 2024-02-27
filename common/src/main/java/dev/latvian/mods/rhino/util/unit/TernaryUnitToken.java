package dev.latvian.mods.rhino.util.unit;

import java.util.Objects;

public class TernaryUnitToken implements UnitToken {

    private final UnitToken cond;
    private final UnitToken ifTrue;
    private final UnitToken ifFalse;
    public TernaryUnitToken(UnitToken cond, UnitToken ifTrue, UnitToken ifFalse) {
        this.cond = cond;
        this.ifTrue = ifTrue;
        this.ifFalse = ifFalse;
    }
    public UnitToken cond() {
        return this.cond;
    }
    public UnitToken ifTrue() {
        return this.ifTrue;
    }
    public UnitToken ifFalse() {
        return this.ifFalse;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof TernaryUnitToken)) {
            return false;
        }
        TernaryUnitToken other = (TernaryUnitToken) obj;
        return
            this.cond == other.cond &&
            this.ifTrue == other.ifTrue &&
            this.ifFalse == other.ifFalse;
    }
    @Override
    public int hashCode() {
        return Objects.hash(cond, ifTrue, ifFalse);
    }
	@Override
	public Unit interpret(UnitTokenStream stream) {
		return new TernaryUnit(cond.interpret(stream), ifTrue.interpret(stream), ifFalse.interpret(stream));
	}

	@Override
	public String toString() {
		return "(" + cond + " ? " + ifTrue + " : " + ifFalse + ")";
	}
}
