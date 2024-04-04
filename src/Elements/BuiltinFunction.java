package Elements;

import ErrorManager.Error;
import ErrorManager.ErrorManager;
import Interpreter.State;

public abstract class BuiltinFunction extends Function {
    BuiltinFunction(FunctionType type) {
        this.type = type;
    }
    @Override
    public Value apply(ErrorManager errorManager, Value... values) {
        ExpressionFunction.validateArguments(errorManager, type, values);

        return prevalidatedApply(errorManager, values);
    }

    public abstract Value prevalidatedApply(ErrorManager errorManager, Value[] values);
}
