package Elements;

import java.util.Map;
import java.util.Objects;

public class BuiltinValue extends Value {
    public BuiltinValue(Type type) {
        super();
        this.type = type;
    }

    public String toString() {
        for (Map.Entry<String, Value> entry : ValueLibrary.builtinValues.entrySet()) {
            if (this == entry.getValue()) {
                return entry.getKey();
            }
        }
        return super.toString();
    }
}
