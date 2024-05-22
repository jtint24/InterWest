package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import Parser.ParseTreeNode;
import Utils.Result;

import java.util.ArrayList;

public class ReturnableExpressionSeries extends ExpressionContainer {
    ArrayList<Expression> subExpressions;

    Type returnType;

    public ReturnableExpressionSeries(Type returnType, ArrayList<Expression> subExpressions) {
        this.returnType = returnType;
        this.subExpressions = subExpressions;
        this.underlyingParseTree = null; // A SINGLE PARSE NODE CAN'T REPRESENT A SERIES OF EXPRESSIONS
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
        throw new RuntimeException("This expression should have returned by now!");
        // return null;
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
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        StaticReductionContext oldContext = context;

        for (Expression subExpression : subExpressions) {
            context = subExpression.initializeStaticValues(context);
            if (context.returnedValue != null) {
                staticValue = context.returnedValue;
            }
        }

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
