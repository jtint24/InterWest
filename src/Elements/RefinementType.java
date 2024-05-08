package Elements;

import ErrorManager.ErrorManager;
import Interpreter.ExpressionResult;
import Interpreter.State;

public class RefinementType extends Type {
    Type superType;

    Function condition;

    public RefinementType(Type superType, Function condition) {
        this.superType = superType;
        this.condition = condition;
    }

    @Override
    public boolean subtypeOf(Type superType) {
        if (superType instanceof UniverseType || superType==this) {
            return true;
        } else {
            return this.superType.subtypeOf(superType);
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
