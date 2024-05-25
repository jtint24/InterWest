package Elements;

import ErrorManager.ErrorManager;
import Interpreter.Expression;
import Interpreter.State;
import Regularity.DFA;

public abstract class DFAFunction extends Function {
    // This function acts like a passthrough for a stored function but also adds the ability to assign a DFA

    Function wrappedFunction;

    public DFAFunction(Function wrappedFunction) {
        super();
        this.wrappedFunction = wrappedFunction;
    }

    @Override
    public Value apply(ErrorManager errorManager, Value... values) {
        return wrappedFunction.apply(errorManager, values);
    }

    public abstract DFA getDFA(int wrtArg, Value... inputs);

    public FunctionType getType() {
        return wrappedFunction.getType();
    }
}
