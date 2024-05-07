package Interpreter;

import Elements.Function;
import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import Utils.Result;
import ErrorManager.Error;

import java.util.ArrayList;

public class FunctionExpression extends Expression {
    Function wrappedFunction;

    ArrayList<Expression> inputExpressions;

    public FunctionExpression(Function wrappedFunction, ArrayList<Expression> inputExpressions) {
        this.wrappedFunction = wrappedFunction;
        this.inputExpressions = inputExpressions;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        ArrayList<Value> results = new ArrayList<>();
        for (Expression inputExpression : inputExpressions) {
            ExpressionResult result = inputExpression.evaluate(situatedState);
            results.add(result.resultingValue);
            situatedState = result.resultingState;
        }

        Value functionResult = wrappedFunction.apply(situatedState.errorManager, results.toArray(new Value[0]));

        return new ExpressionResult(situatedState, functionResult);
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        for (Expression inputExpression : inputExpressions) {
            context = inputExpression.validate(context);
        }

        // TODO: validate that the types are valid
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return wrappedFunction.getType().getResultType();
    }

    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        staticValue = Result.error(Error.runtimeWarning("Function expressions cannot be statically reduced"));
        return context;
    }
}
