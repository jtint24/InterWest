package Interpreter;

import Elements.Type;
import Elements.ValueLibrary;

import java.util.ArrayList;

public class ExpressionSeries extends Expression {
    ArrayList<Expression> subExpressions;

    public ExpressionSeries(ArrayList<Expression> subExpressions) {
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
                return result;
            }
            situatedState = result.resultingState;
        }

        return new ExpressionResult(situatedState, ValueLibrary.trueValue);
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        context.addScope();
        for (Expression expr : subExpressions) {
            context = expr.validate(context);
        }
        context.killScope();
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return ValueLibrary.boolType;
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
