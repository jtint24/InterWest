package Elements;

import ErrorManager.ErrorManager;
import Utils.TriValue;

public class UniverseType extends Type {
    @Override
    public TriValue subtypeOf(Type superType) {
        if (superType instanceof UniverseType) {
            return TriValue.TRUE;
        } else {
            return TriValue.FALSE;
        }
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        return true;
    }
}
