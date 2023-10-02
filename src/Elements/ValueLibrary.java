package Elements;

import java.util.HashMap;

public class ValueLibrary {
    public static Type typeType = new Type();
    public static Type boolType = new Type();
    public static Type intType = new Type();
    public static BuiltinValue trueValue = new BuiltinValue(boolType);
    public static BuiltinValue falseValue = new BuiltinValue(boolType);
    public static HashMap<String, Value> builtinValues = new HashMap<>() {{
        put("true", trueValue);
        put("false", falseValue);
        put("Type", typeType);
        put("Bool", boolType);
        put("Int", intType);
    }};
}
