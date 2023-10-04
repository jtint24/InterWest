package Interpreter;

import Elements.Type;

import java.util.ArrayList;

public class ReturnableExpressionSeries extends Expression {
    ArrayList<Expression> subExpressions;

    Type returnType;

    public ReturnableExpressionSeries(Type returnType, ArrayList<Expression> subExpressions) {
        this.returnType = returnType;
        this.subExpressions = subExpressions;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        situatedState.addScope();

        for (Expression expr : subExpressions) {
            ExpressionResult result = expr.evaluate(situatedState);
            // System.out.println(result.resultingState);
            if (result.getEarlyReturn()) {
                result.resultingState.killScope();
                result.setEarlyReturn(false);
                return result;
            }
            situatedState = result.resultingState;
        }

        return null;
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        // TODO: Ensure that there's no fallthrough; every branch has to end with a return

        context.addScope();
        context.setReturnType(returnType);
        for (Expression expr : subExpressions) {
            context = expr.validate(context);
        }
        context.killScope();
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return returnType;
    }

    @Override
    public String toString() {
        StringBuilder retString = new StringBuilder();
        for (Expression subExpression : subExpressions) {
            retString.append(subExpression);
        }
        return retString.toString();
    }
}
