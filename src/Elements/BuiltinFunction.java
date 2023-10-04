package Elements;

import ErrorManager.Error;
import ErrorManager.ErrorManager;
import Interpreter.State;

public abstract class BuiltinFunction extends Function {
    BuiltinFunction(FunctionType type) {
        this.type = type;
    }
    @Override
    public Value apply(ErrorManager errorManager, State state, Value... values) {
        if (values.length != type.parameterTypes.length) {
            errorManager.logError(new Error(Error.ErrorType.RUNTIME_ERROR, "Expected `"+type.parameterTypes.length+"` arguments, got `"+values.length+"`", true));
        }

        for (int i = 0; i<values.length; i++) {
            if (!type.parameterTypes[i].matchesValue(values[i].getType(), errorManager)) {
                errorManager.logError(new Error(Error.ErrorType.RUNTIME_ERROR, "Type mismatch in parameter "+(i+1)+". Expected "+type.parameterTypes[i]+" got "+values[i].getType()+".", true));
            }
        }

        return prevalidatedApply(errorManager, values);
    }

    public abstract Value prevalidatedApply(ErrorManager errorManager, Value[] values);
}
