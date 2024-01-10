package Elements;

import ErrorManager.ErrorManager;
import Interpreter.State;
import Regularity.DFA;
import Regularity.DFAConditions;
import Utils.Result;

import java.util.HashMap;

public class ValueLibrary {
    public static Type universeType = new UniverseType();
    public static RefinementType typeType = new RefinementType(universeType, null);
    public static RefinementType boolType = new RefinementType(universeType, null);
    public static RefinementType intType = new RefinementType(universeType, null);
    public static ValueWrapper<Boolean> trueValue = new ValueWrapper<>(true, boolType);
    public static ValueWrapper<Boolean> falseValue = new ValueWrapper<>(false, boolType);

    public static Function equalsFunc = new BuiltinFunction(new FunctionType(boolType, universeType, universeType)) {
        @Override
        public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
            return values[0].equals(values[1]) ? trueValue : falseValue;
        }
    };
    public static HashMap<String, Value> builtinValues = new HashMap<>() {{
        put("true", trueValue);
        put("false", falseValue);
        put("Type", typeType);
        put("Bool", boolType);
        put("Int", intType);
        put("Universe", universeType);
    }};

    static {
        boolType.condition = new BuiltinFunction(new FunctionType(boolType, universeType)) {
            @Override
            public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
                Value inputVal = values[0];
                return (inputVal == trueValue || inputVal == falseValue) ? trueValue : falseValue;
            }
        };
        typeType.condition = new BuiltinFunction(new FunctionType(boolType, universeType)) {
            @Override
            public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
                Value inputVal = values[0];
                return new ValueWrapper<>(inputVal instanceof Type, boolType);
            }
        };


    }
}
