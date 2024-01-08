package Interpreter;

import Elements.Type;
import Elements.Value;
import ErrorManager.Error;
import Utils.Result;

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
        if (!context.hasVariable(identifier)) {
            context.addError(new Error(Error.ErrorType.INTERPRETER_ERROR, "Can't find variable `"+identifier+"` in scope.", true));
        }
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return context.getVariableType(identifier);
    }

    @Override
    public Result<Value, Exception> reduceToValue() {
        // TODO: Track the variable to its definition to see if it's statically checkable

        return Result.error(new Exception("Variables can't be reduced to static values yet"));
    }

    @Override
    public String toString() {
        return identifier;
    }
}
