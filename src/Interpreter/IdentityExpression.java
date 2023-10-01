package Interpreter;

import Elements.BuiltinValue;
import Elements.Value;

public class IdentityExpression extends Expression {
    Value wrappedValue;

    public IdentityExpression(Value wrappedValue) {
        this.wrappedValue = wrappedValue;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        return new ExpressionResult(situatedState, wrappedValue);
    }
}
