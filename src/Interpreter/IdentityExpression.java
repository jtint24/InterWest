package Interpreter;

import Elements.BuiltinValue;
import Elements.Type;
import Elements.Value;
import Utils.Result;

public class IdentityExpression extends Expression {
    Value wrappedValue;

    public IdentityExpression(Value wrappedValue) {
        this.wrappedValue = wrappedValue;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        return new ExpressionResult(situatedState, wrappedValue);
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return wrappedValue.getType();
    }

    @Override
    public Result<Value, Exception> reduceToValue() {
        return Result.ok(wrappedValue);
    }

    @Override
    public String toString() {
        return wrappedValue.toString();
    }
}
