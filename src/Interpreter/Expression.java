package Interpreter;

import Elements.Type;
import Elements.Value;
import Utils.Result;

public abstract class Expression {
    public abstract ExpressionResult evaluate(State situatedState);
    public abstract ValidationContext validate(ValidationContext context);

    /**
     * getType
     *
     * Gets the result type of the expression, except in case of early return (in which case the result type of the
     * expression may not match this type
     * */
    public abstract Type getType(ValidationContext context);

    public abstract Result<Value, Exception> reduceToValue();
}
