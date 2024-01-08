package Interpreter;

import Elements.Type;
import Elements.Value;
import Utils.Result;

public class LambdaExpression extends Expression {

    @Override
    public ExpressionResult evaluate(State situatedState) {
        return null;
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        return null;
    }

    @Override
    public Type getType(ValidationContext context) {
        return null;
    }

    @Override
    public Result<Value, Exception> reduceToValue() {
        return null;
    }

}
