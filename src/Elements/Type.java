package Elements;

import ErrorManager.ErrorManager;

import java.util.Map;

public abstract class Type extends Value {
    public abstract boolean subtypeOf(Type superType);

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
