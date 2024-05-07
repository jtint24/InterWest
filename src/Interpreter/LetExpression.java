package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.Error;
import Utils.Result;

public class LetExpression extends Expression {
    String identifierName;
    Expression exprToSet;

    public LetExpression(String identifierName, Expression exprToSet) {
        this.identifierName = identifierName;
        this.exprToSet = exprToSet;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        ExpressionResult assignResult = exprToSet.evaluate(situatedState);

        State newState = assignResult.resultingState;
        Value valueToAssign = assignResult.resultingValue;

        newState.put(identifierName, valueToAssign);

        return new ExpressionResult(newState, ValueLibrary.trueValue);
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        context = exprToSet.validate(context);
        if (context.hasVariable(identifierName)) {
            context.addError(new Error(Error.ErrorType.INTERPRETER_ERROR, "Can't redeclare variable `"+identifierName+"`", true));
        }
        context.addVariableType(identifierName, exprToSet.getType(context));
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return ValueLibrary.boolType;
    }

    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        staticValue = Result.ok(ValueLibrary.trueValue);

        StaticReductionContext discardedContext = exprToSet.initializeStaticValues(context);


        // NOTE: We could build some mechanism to chain other errors to this one
        // Like, if this constant is used later, and the static value of it isn't available, then we could link it to
        // the error in the let statement instead of the error in the reference

        if (exprToSet.staticValue.isOK()) {
            context.declaredConstants.put(identifierName, exprToSet.staticValue.getOkValue());
        }



        return context;
    }

    @Override
    public String toString() {
        String line = "let "+identifierName+" = "+exprToSet;
        if (!line.endsWith("\n")) {
            line += "\n";
        }
        return line;
    }
}
