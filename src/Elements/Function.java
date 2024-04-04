package Elements;

import ErrorManager.ErrorManager;
import Interpreter.State;

import java.util.Map;

public abstract class Function extends Value {
    FunctionType type;

    public abstract Value apply(ErrorManager errorManager, Value... values);

    @Override
    public FunctionType getType() {
        return type;
    }

    @Override
    public String toString() {
        for (Map.Entry<String, Value> entry : ValueLibrary.builtinValues.entrySet()) {
            if (this == entry.getValue()) {
                return entry.getKey();
            }
        }
        return super.toString();
    }
}
