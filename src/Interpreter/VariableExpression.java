package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
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
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        if (ValueLibrary.builtinValues.containsKey(identifier)) {
            staticValue = Result.ok(ValueLibrary.builtinValues.get(identifier));
        }

        if (context.declaredConstants.containsKey(identifier)) {
            staticValue = Result.ok(context.declaredConstants.get(identifier));
        }

        return context;
    }

    @Override
    public String toString() {
        return identifier;
    }
}
