package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.Error;
import Utils.Result;
import Utils.TriValue;

public class ReturnExpression extends Expression {

    Expression exprToReturn;

    public ReturnExpression(Expression exprToReturn) {
        this.exprToReturn = exprToReturn;
    }

    public Expression getExprToReturn() {
        return exprToReturn;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        ExpressionResult returnResult = exprToReturn.evaluate(situatedState);
        returnResult.setEarlyReturn(true);
        return returnResult;
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        Type actualReturnType = exprToReturn.getType(context);
        TriValue subtypeStatus = actualReturnType.subtypeOf(context.getReturnType());
        if (subtypeStatus == TriValue.FALSE) {
            context.addError(new Error(Error.ErrorType.INTERPRETER_ERROR, "Type mismatch between expected return type `"+context.getReturnType()+"` and actual return type `"+actualReturnType+"`", true));
        } else if (subtypeStatus == TriValue.UNKNOWN) {
            context.addError(new Error(Error.ErrorType.INTERPRETER_ERROR, "Can't prove match between expected return type `"+context.getReturnType()+"` and actual return type `"+actualReturnType+"`", false));
        }
        context = exprToReturn.validate(context);
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return ValueLibrary.boolType;
    }



    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        StaticReductionContext discardedContext = exprToReturn.initializeStaticValues(context);

        context.returnedValue = exprToReturn.staticValue;

        return context;
    }

    @Override
    public String toString() {
        String line = "return "+exprToReturn;
        if (!line.endsWith("\n")) {
            line += "\n";
        }
        return line;
    }
}
