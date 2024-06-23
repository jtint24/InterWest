package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import Parser.ParseTreeNode;
import Utils.Result;

import java.util.ArrayList;
import java.util.HashMap;

public class ExpressionSeries extends ExpressionContainer {
    ArrayList<Expression> subExpressions;

    public ExpressionSeries(ArrayList<Expression> subExpressions) {
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
                return result;
            }
            situatedState = result.resultingState;
        }

        return new ExpressionResult(situatedState, ValueLibrary.trueValue);
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        HashMap<String, Type> forwardDeclaredTypes = new HashMap<>();

        for (Expression expr : subExpressions) {
            forwardDeclaredTypes.putAll(expr.validateForwardDeclaration(context));
        }

        context.addScope();
        context.addVariableTypes(forwardDeclaredTypes);
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
    public Type getType(StaticReductionContext context) {
        return ValueLibrary.boolType;
    }

    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        staticValue = Result.ok(ValueLibrary.trueValue);
        StaticReductionContext oldContext = context;
        for (Expression subExpression : subExpressions) {
            context = subExpression.initializeStaticValues(context);
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
