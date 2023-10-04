package Elements;

import ErrorManager.Error;
import Interpreter.Expression;
import ErrorManager.ErrorManager;
import Interpreter.ExpressionResult;
import Interpreter.State;

public class ExpressionFunction extends Function {
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
