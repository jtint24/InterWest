package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.Error;
import Utils.Result;

import java.util.ArrayList;


public class ConditionalExpression extends Expression {
    ExpressionSeries expressionSeries;
    Expression condition;

    public ConditionalExpression(ExpressionSeries expressionSeries, Expression condition) {
        this.expressionSeries = expressionSeries;
        this.condition = condition;
    }

    public Expression getCondition() {
        return condition;
    }

    public ExpressionSeries getBody() {
        return expressionSeries;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        ExpressionResult conditionResult = condition.evaluate(situatedState);
        situatedState = conditionResult.resultingState;

        if (conditionResult.resultingValue == ValueLibrary.trueValue) {
            return expressionSeries.evaluate(situatedState);
        }

        return conditionResult;
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        context = condition.validate(context);

        context.addScope();
        for (Expression innerExpression : expressionSeries.getContainedExpressions()) {
            context = innerExpression.validate(context);
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
        return context;
    }


    @Override
    public String toString() {
        return "if "+condition+" {\n"+expressionSeries+"}";
    }

    /*

    @Override
    public ArrayList<Expression> getContainedExpressions() {
        return new ArrayList<>() {{
            add(expressionSeries);
        }};
    }

     */
}
