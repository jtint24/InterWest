package Interpreter;

import Elements.*;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Utils.Result;
import ErrorManager.Error;
import Utils.TriValue;

import java.util.ArrayList;

public class FunctionExpression extends Expression {
    Expression funcExpression; // Expression to evaluate to get the function

    ArrayList<Expression> inputExpressions;

    public FunctionExpression(Expression funcExpression, ArrayList<Expression> inputExpressions) {
        this.funcExpression = funcExpression;
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

        ExpressionResult innerFuncResult = funcExpression.evaluate(situatedState);

        situatedState = innerFuncResult.resultingState;

        Function wrappedFunction = (Function) innerFuncResult.resultingValue;

        Value functionResult = wrappedFunction.apply(situatedState.errorManager, results.toArray(new Value[0]));

        return new ExpressionResult(situatedState, functionResult);
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        for (Expression inputExpression : inputExpressions) {
            context = inputExpression.validate(context);
        }

        Type funcType = funcExpression.getType(context);
        if (!(funcType instanceof FunctionType)) {
            context.addError(
                    new Error(Error.ErrorType.INTERPRETER_ERROR, "Expression of type " + funcType + " is not callable.", true)
            );

            return context;
        }

        Type[] parameterTypes = ((FunctionType) funcType).getParameterTypes();
        for (int i = 0; i<parameterTypes.length; i++) {
            Type parameterType = parameterTypes[i];
            Type argumentType = inputExpressions.get(i).getType(context);

            TriValue subtypeStatus = inputExpressions.get(i).matchesType(parameterType, context);

            if (subtypeStatus == TriValue.FALSE) {
                context.addError(
                        new Error(Error.ErrorType.INTERPRETER_ERROR,  "Expected type "+parameterType+" doesn't match received type "+argumentType+" in argument "+(i+1)+".", true)
                );
            } else if (subtypeStatus == TriValue.UNKNOWN) {
                context.addError(
                            new Error(Error.ErrorType.INTERPRETER_ERROR, "Can't prove that expected type " + parameterType + " matches received type " + argumentType + " in argument " + (i + 1) + ".", false)
                );
            }
        }

        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        Type funcType = funcExpression.getType(context);

        if (funcType instanceof FunctionType) {
            return ((FunctionType) funcType).getResultType();
        } else {
            return null;
        }
    }

    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        staticValue = Result.error(Error.runtimeWarning("Function expressions cannot be statically reduced"));
        for (Expression inputExpression : inputExpressions) {
            context = inputExpression.initializeStaticValues(context);
        }
        return context;
    }
}
