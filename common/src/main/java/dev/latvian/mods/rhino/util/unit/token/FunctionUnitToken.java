package dev.latvian.mods.rhino.util.unit.token;

import java.util.List;
import java.util.Objects;

import dev.latvian.mods.rhino.util.unit.Unit;
import dev.latvian.mods.rhino.util.unit.function.FunctionFactory;

public class FunctionUnitToken implements UnitToken{

    private final String name;
    private final List<UnitToken> args;
    public FunctionUnitToken(String name, List<UnitToken> args) {
        this.name = name;
        this.args = args;
    }
    public String name() {
        return this.name;
    }
    public List<UnitToken> args() {
        return this.args;
    }
    @Override
    public String toString() {
        return String.format("FunctionUnitToken {%s, %s}", name, args);
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof FunctionUnitToken)) {
            return false;
        }
        FunctionUnitToken other = (FunctionUnitToken) obj;
        return
            this.name == other.name &&
            this.args == other.args;
    }
    @Override
    public int hashCode() {
        return Objects.hash(name, args);
    }

	@Override
	public Unit interpret(UnitTokenStream stream) {
		FunctionFactory factory = stream.context.getFunctionFactory(name);

		if (factory == null) {
			throw new IllegalStateException("Unknown function '" + name + "'!");
		} else if (args.isEmpty()) {
			return factory.create(Unit.EMPTY_ARRAY);
		}

		Unit[] newArgs = new Unit[args.size()];

		for (int i = 0; i < args.size(); i++) {
			newArgs[i] = args.get(i).interpret(stream);
		}

		return factory.create(newArgs);
	}
}
