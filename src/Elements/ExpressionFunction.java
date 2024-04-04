package Elements;

import Interpreter.Expression;
import ErrorManager.ErrorManager;
import ErrorManager.Error;
import Interpreter.ExpressionResult;
import Interpreter.State;

public class ExpressionFunction extends Function {
    /**
     * THIS CLASS MAY NEED TO BE DELETED!
     *
     * Frankly, I'm not entirely sure that functions need to be able to wrap expressions, especially if expressions
     * can wrap functions. If you delete this, MAKE SURE to delete the 'state' parameter from the apply function in
     * the base Function class. I don't think there's anywhere else it's needed.
     *
     * Update Apr 3, 2024
     *
     * I now think this is needed. Lambda functions have their functionality depend on expressions. The proper thing
     * to do here, I think, is to have an ExpressionFunction containing all the expressions that make up the lambda.
     * Then that ExpressionFunction is wrapped in an IdentityExpression to allow the lambda to be passed around and
     * used like a value. It may actually be FunctionExpression, which wraps a function in an expression, that may need
     * to be deleted.
     *
     * That being said, I'm eliminating the 'state' parameter from apply anyways. This way, lambdas WILL NOT inherit
     * scope from their context, making them true lambdas and not closures. This will make them referentially
     * transparent and will probably make DFA conversion easier too. It'll make it more of a pain for coders writing
     * in it, but honestly it's a tradeoff I'm willing to make
     * */

    FunctionType type;
    Expression wrappedExpression;
    String[] parameterNames;

    public ExpressionFunction(FunctionType type, Expression wrappedExpression, String[] parameterNames) {
        this.type = type;
        this.wrappedExpression = wrappedExpression;
        this.parameterNames = parameterNames;
    }

    @Override
    public Value apply(ErrorManager errorManager, Value... values) {
        validateArguments(errorManager, type, values);

        State state = new State(errorManager);
        for (int i = 0; i<values.length; i++) {
            state.put(parameterNames[i], values[i]);
        }


        ExpressionResult result = wrappedExpression.evaluate(state);

        state.killScope();

        return result.resultingValue;
    }

    static void validateArguments(ErrorManager errorManager, FunctionType type, Value[] values) {
        if (values.length != type.parameterTypes.length) {
            errorManager.logError(new Error(Error.ErrorType.RUNTIME_ERROR, "Expected `"+ type.parameterTypes.length+"` arguments, got `"+values.length+"`", true));
        }

        for (int i = 0; i<values.length; i++) {
            if (!type.parameterTypes[i].matchesValue(values[i].getType(), errorManager)) {
                errorManager.logError(new Error(Error.ErrorType.RUNTIME_ERROR, "Type mismatch in parameter "+(i+1)+". Expected "+ type.parameterTypes[i]+" got "+values[i].getType()+".", true));
            }
        }
    }
}
