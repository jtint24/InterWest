package Interpreter;

import Elements.*;
import Parser.ParseTreeNode;
import Utils.Result;
import ErrorManager.Error;
import Utils.TriValue;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static ErrorManager.ErrorLibrary.*;

public class FunctionExpression extends Expression {
    Expression funcExpression; // Expression to evaluate to get the function

    ArrayList<Expression> inputExpressions;

    public FunctionExpression(Expression funcExpression, ArrayList<Expression> inputExpressions, ParseTreeNode underlyingParseTree) {
        this.funcExpression = funcExpression;
        this.inputExpressions = inputExpressions;
        this.underlyingParseTree = underlyingParseTree;
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
        context = funcExpression.validate(context);

        for (Expression inputExpression : inputExpressions) {
            context = inputExpression.validate(context);
        }

        Type funcType = funcExpression.getType(context);
        if (!(funcType instanceof FunctionType)) {
            context.addError(
                    getCallabilityMismatch(this, funcType)
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
                        getFunctionArgumentTypeMismatch(this, inputExpressions.get(i), parameterType, argumentType)
                );
            } else if (subtypeStatus == TriValue.UNKNOWN) {
                context.addError(
                        getFunctionArgumentTypeWarning(this, inputExpressions.get(i), parameterType, argumentType)
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
        staticValue = Result.error("Function expressions cannot be statically reduced");
        for (Expression inputExpression : inputExpressions) {
            context = inputExpression.initializeStaticValues(context);
        }
        return context;
    }

    @Override
    public String toString() {
        List<String> inputExprStrings = inputExpressions.stream().map(Expression::toString).collect(Collectors.toList());
        return funcExpression+"("+String.join(", ", inputExprStrings)+")";
    }

    public Expression getFuncExpression() {
        return funcExpression;
    }
}
