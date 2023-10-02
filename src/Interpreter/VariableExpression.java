package Interpreter;

import Elements.Type;

public class VariableExpression extends Expression {
    String identifier;

    public VariableExpression(String identifier) {
        this.identifier = identifier;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        return new ExpressionResult(situatedState, situatedState.get(identifier));
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return context.getVariableType(identifier);
    }

    @Override
    public String toString() {
        return identifier;
    }
}
