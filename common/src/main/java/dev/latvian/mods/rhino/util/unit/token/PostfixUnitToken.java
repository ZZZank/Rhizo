package dev.latvian.mods.rhino.util.unit.token;

import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.Stack;

import dev.latvian.mods.rhino.util.unit.Unit;

public class PostfixUnitToken implements UnitToken{

    private final List<UnitToken> infix;
    public PostfixUnitToken(List<UnitToken> infix) {
        this.infix = infix;
    }
    public List<UnitToken> infix() {
        return this.infix;
    }
    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof PostfixUnitToken)) {
            return false;
        }
        PostfixUnitToken other = (PostfixUnitToken) obj;
        return
            this.infix == other.infix;
    }
    @Override
    public int hashCode() {
        return Objects.hash(infix);
    }
	@Override
	public Unit interpret(UnitTokenStream stream) {
		Stack<UnitSymbol> operatorsStack = new Stack<>();
		LinkedList<UnitToken> postfix = new LinkedList<>();

		for (UnitToken next : infix) {
			if (stream.context.isDebug()) {
				stream.context.debugInfo("> " + next);
			}

			if (next instanceof UnitSymbol) {
			    UnitSymbol nextOperator = (UnitSymbol) next;
				boolean pushedCurrent = false;

				while (!operatorsStack.isEmpty()) {
					UnitSymbol o = operatorsStack.peek();

					if (o != null) {
						if (o.hasHigherPrecedenceThan(nextOperator)) {
							postfix.add(operatorsStack.pop());

							if (stream.context.isDebug()) {
								stream.context.debugInfo("Operator Stack", operatorsStack);
								stream.context.debugInfo("Operand Stack", postfix);
							}
						} else {
							pushedCurrent = true;
							operatorsStack.push(nextOperator);

							if (stream.context.isDebug()) {
								stream.context.debugInfo("Operator Stack", operatorsStack);
								stream.context.debugInfo("Operand Stack", postfix);
							}

							break;
						}
					} else {
						break;
					}
				}

				if (!pushedCurrent) {
					operatorsStack.push(nextOperator);

					if (stream.context.isDebug()) {
						stream.context.debugInfo("Operator Stack", operatorsStack);
						stream.context.debugInfo("Operand Stack", postfix);
					}
				}
			} else {
				postfix.add(next);

				if (stream.context.isDebug()) {
					stream.context.debugInfo("Operator Stack", operatorsStack);
					stream.context.debugInfo("Operand Stack", postfix);
				}
			}
		}

		while (!operatorsStack.isEmpty()) {
			UnitSymbol last = operatorsStack.pop();
			postfix.add(last);
		}

		if (stream.context.isDebug()) {
			stream.context.debugInfo("Postfix", postfix);
		}

		Stack<UnitToken> resultStack = new Stack<UnitToken>();

		for (UnitToken token : postfix) {
			token.unstack(resultStack);

			if (stream.context.isDebug()) {
				stream.context.debugInfo("Result Stack", resultStack);
			}
		}

		UnitToken lastUnit = resultStack.pop();
		return lastUnit.interpret(stream);
	}

	public UnitToken normalize() {
		if (infix.size() == 1) {
			return infix.get(0);
		} else if (infix.size() == 3 && infix.get(1) instanceof UnitSymbol) {
			UnitSymbol symbol = (UnitSymbol) infix.get(1);
			if (symbol.op != null) {
				return new OpResultUnitToken(symbol, infix.get(0), infix.get(2));
			}
		}

		return this;
	}

	@Override
	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append('(');

		for (int i = 0; i < infix.size(); i++) {
			if (i > 0) {
				sb.append(' ');
			}

			sb.append(infix.get(i));
		}

		sb.append(')');
		return sb.toString();
	}
}
