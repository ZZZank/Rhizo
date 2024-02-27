package dev.latvian.mods.rhino.util.unit.token;

import java.util.Stack;

import dev.latvian.mods.rhino.util.unit.Unit;

public interface UnitToken {
	default Unit interpret(UnitTokenStream stream) {
		return (Unit) this;
	}

	default boolean nextUnaryOperator() {
		return false;
	}

	default void unstack(Stack<UnitToken> resultStack) {
		resultStack.push(this);
	}
}
