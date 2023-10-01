package Interpreter;

import java.util.ArrayList;

public class ExpressionSeries extends Expression {
    ArrayList<Expression> subExpressions;

    public ExpressionSeries(ArrayList<Expression> subExpressions) {
        this.subExpressions = subExpressions;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        for (Expression expr : subExpressions) {
            ExpressionResult result = expr.evaluate(situatedState);
            if (expr instanceof ReturnExpression) {
                return result;
            }
            situatedState = result.resultingState;
        }

        return null;
    }
}
