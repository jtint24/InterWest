package Elements;

import ErrorManager.ErrorManager;
import Interpreter.ExpressionResult;
import Interpreter.State;
import Utils.TriValue;

public class RefinementType extends Type {
    Type superType;

    Function condition;

    public RefinementType(Type superType, Function condition) {
        this.superType = superType;
        this.condition = condition;
    }

    @Override
    public TriValue subtypeOf(Type superType) {

        if (superType instanceof UniverseType) {
            return TriValue.TRUE;
        }

        TriValue parentIsSubtype = this.superType.subtypeOf(superType);

        if (parentIsSubtype == TriValue.TRUE) {
            return TriValue.TRUE;
        }

        if (condition instanceof DFAFunction && superType instanceof RefinementType && ((RefinementType) superType).condition instanceof DFAFunction) {
            // TODO: Implement:
            // IF I can turn superType into a DFA and I can turn MYSELF into a DFA, then do that
            // Then compare superType's DFA to mine and see if it's a superset language

            // If it's not a superset, then I'm NOT a subtype

            return TriValue.UNKNOWN;
        } else {
            // If I can't do the conversion, then I don't know if I'm a subtype at all!

            return TriValue.UNKNOWN;
        }
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        if (superType.matchesValue(v, errorManager)) {
            Value result = condition.apply(errorManager, v);

            return result.equals(ValueLibrary.trueValue);
        } else {
            return false;
        }
    }
}
