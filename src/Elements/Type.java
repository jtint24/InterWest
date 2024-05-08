package Elements;

import ErrorManager.ErrorManager;
import Utils.TriValue;

import java.util.Map;

public abstract class Type extends Value {
    public abstract TriValue subtypeOf(Type superType);

    public abstract boolean matchesValue(Value v, ErrorManager errorManager);

    public Type getType() {
        return ValueLibrary.typeType;
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
