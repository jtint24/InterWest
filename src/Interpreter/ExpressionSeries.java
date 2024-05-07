package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import Utils.Result;

import java.util.ArrayList;

public class ExpressionSeries extends ExpressionContainer {
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
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        staticValue = Result.ok(ValueLibrary.trueValue);
        StaticReductionContext oldContext = context;
        for (Expression subExpression : subExpressions) {
            context = subExpression.initializeStaticValues(context);
        }
        oldContext.addErrors(context.errors);
        return oldContext;
    }

    @Override
    public String toString() {
        StringBuilder retString = new StringBuilder();
        for (Expression subExpression : subExpressions) {
            retString.append(subExpression);
        }
        return retString.toString();
    }

    @Override
    public ArrayList<Expression> getContainedExpressions() {
        return subExpressions;
    }
}
