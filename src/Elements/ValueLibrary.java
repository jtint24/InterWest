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
    public static RefinementType unitType = new RefinementType(universeType, null);
    public static RefinementType intType = new RefinementType(universeType, null);
    public static ValueWrapper<Boolean> trueValue = new ValueWrapper<>(true, boolType);
    public static ValueWrapper<Boolean> falseValue = new ValueWrapper<>(false, boolType);
    public static BuiltinValue unitValue = new BuiltinValue(unitType);

    public static Function printInt = new BuiltinFunction(new FunctionType(boolType, intType)) {
        @Override
        public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
            System.out.println(values[0]);
            return trueValue;
        }
    };
    public static RefinementType nonzeroType = new RefinementType(universeType, null);
    public static Function printNonzero = new BuiltinFunction(new FunctionType(boolType, nonzeroType)) {
        @Override
        public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
            System.out.println(values[0]);
            return trueValue;
        }
    };
    public static Function equalsFunc = new BuiltinFunction(new FunctionType(boolType, universeType, universeType)) {
        @Override
        public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
            return values[0].equals(values[1]) ? trueValue : falseValue;
        }
    };

    public static Function unequalsFunc =new DFAFunction(
            new BuiltinFunction(new FunctionType(boolType, universeType, universeType)) {
                @Override
                public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
                    return !values[0].equals(values[1]) ? trueValue : falseValue;
                }
            }
    ) {
        @Override
        public DFA getDFA(int wrtArg, Value... inputs) {
            return DFAConditions.dfaInequalTo(inputs[0]);
        }
    };

    public static HashMap<String, Value> builtinValues = new HashMap<>() {{
        put("true", trueValue);
        put("false", falseValue);
        put("Type", typeType);
        put("Bool", boolType);
        put("Int", intType);
        put("Universe", universeType);
        put("printInt", printInt);
        put("printNonzero", printNonzero);
        put("Nonzero", nonzeroType);
        put("Unit", unitType);
        put("unit", unitValue);
        put("!=", unequalsFunc);
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
        intType.condition = new DFAFunction(new BuiltinFunction(new FunctionType(boolType, universeType)) {
            @Override
            public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
                Value inputVal = values[0];
                return new ValueWrapper<>(inputVal instanceof ValueWrapper && ((ValueWrapper<?>) inputVal).wrappedValue instanceof Integer, boolType);
            }
        }) {
            @Override
            public DFA getDFA(int wrtArg, Value... inputs) {
                return DFA.alwaysTrue();
            }
        };

        nonzeroType.condition = new DFAFunction(
                new BuiltinFunction(new FunctionType(boolType, intType)
                ) {
                    @Override
                    public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
                        Value inputVal = values[0];
                        System.out.println("Testing nonzero of "+inputVal);
                        boolean result = !(inputVal instanceof ValueWrapper && ((ValueWrapper<?>) inputVal).wrappedValue.equals(0));
                        return new ValueWrapper<>(result, boolType);
                    }
                }
        ) {
            @Override
            public DFA getDFA(int wrtArg, Value... inputs) {
                // return DFA.alwaysFalse();
                return DFAConditions.dfaInequalTo(new ValueWrapper<>(0, intType));
            }
        };


        unitType.condition = new DFAFunction(new BuiltinFunction(new FunctionType(boolType, universeType)) {
            @Override
            public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
                Value inputVal = values[0];
                return new ValueWrapper<>(inputVal.equals(unitValue), boolType);
            }
        }) {
            @Override
            public DFA getDFA(int wrtArg, Value... inputs) {
                return DFAConditions.dfaEqualTo(unitValue);
            }
        };


    }
}
