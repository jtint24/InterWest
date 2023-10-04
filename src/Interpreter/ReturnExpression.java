package Interpreter;

import Elements.Type;
import Elements.ValueLibrary;
import ErrorManager.Error;

public class ReturnExpression extends Expression {

    Expression exprToReturn;

    public ReturnExpression(Expression exprToReturn) {
        this.exprToReturn = exprToReturn;
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
        if (!actualReturnType.subtypeOf(context.getReturnType())) {
            context.addError(new Error(Error.ErrorType.INTERPRETER_ERROR, "Type mismatch between expected return type `"+context.getReturnType()+"` and actual return type `"+actualReturnType+"`", true));
        }
        context = exprToReturn.validate(context);
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return ValueLibrary.boolType;
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
