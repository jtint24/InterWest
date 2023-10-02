package Interpreter;

import Elements.Type;

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
}
