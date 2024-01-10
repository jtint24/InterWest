package Elements;

import ErrorManager.Error;
import Interpreter.Expression;
import ErrorManager.ErrorManager;
import Interpreter.ExpressionResult;
import Interpreter.State;

public class ExpressionFunction extends Function {
    /**
     * THIS CLASS MAY NEED TO BE DELETED!
     *
     * Frankly, I'm not entirely sure that functions need to be able to wrap expressions, especially if expressions
     * can wrap functions. If you delete this, MAKE SURE to delete the 'state' parameter from the apply function in
     * the base Function class. I don't think there's anywhere else it's needed. 
     * */

    FunctionType type;
    Expression wrappedExpression;
    String[] parameterNames;

    public Value apply(ErrorManager errorManager, State state, Value... values) {
        if (values.length != type.parameterTypes.length) {
            errorManager.logError(new Error(Error.ErrorType.RUNTIME_ERROR, "Expected `"+type.parameterTypes.length+"` arguments, got `"+values.length+"`", true));
        }

        for (int i = 0; i<values.length; i++) {
            if (!type.parameterTypes[i].matchesValue(values[i].getType(), errorManager)) {
                errorManager.logError(new Error(Error.ErrorType.RUNTIME_ERROR, "Type mismatch in parameter "+(i+1)+". Expected "+type.parameterTypes[i]+" got "+values[i].getType()+".", true));
            }
        }

        state.addScope();
        for (int i = 0; i<values.length; i++) {
            state.put(parameterNames[i], values[i]);
        }

        ExpressionResult result = wrappedExpression.evaluate(state);

        state.killScope();

        return result.resultingValue;
    }
}
