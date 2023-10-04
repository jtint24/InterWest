package Elements;

import ErrorManager.ErrorManager;

public class UniverseType extends Type {
    @Override
    public boolean subtypeOf(Type superType) {
        return superType instanceof UniverseType;
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        return true;
    }
}
