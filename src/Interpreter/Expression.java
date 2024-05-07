package Interpreter;

import Elements.Type;
import Elements.Value;
import ErrorManager.Error;
import Utils.Result;

public abstract class Expression {
    Result<Value, Error> staticValue;
    public abstract ExpressionResult evaluate(State situatedState);
    public abstract ValidationContext validate(ValidationContext context);

    /**
     * getType
     *
     * Gets the result type of the expression, except in case of early return (in which case the result type of the
     * expression may not match this type)
     * */
    public abstract Type getType(ValidationContext context);

    /**
     * reduceToValue
     *
     * Give the static result of the expression, if it exists
     * */
    public Result<Value, Error> reduceToValue() {
        return staticValue;
    }

    public abstract StaticReductionContext initializeStaticValues(StaticReductionContext context);
}
