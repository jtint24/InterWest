package Interpreter;

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
            if (expr instanceof ReturnExpression) {
                result.resultingState.killScope();
                return result;
            }
            situatedState = result.resultingState;
        }

        return null;
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
