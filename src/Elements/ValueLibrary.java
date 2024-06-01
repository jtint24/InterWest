package Elements;

import ErrorManager.ErrorManager;
import Regularity.DFA;
import Regularity.DFAConditions;

import java.util.HashMap;

public class ValueLibrary {
    public static Type universeType = new UniverseType();
    public static RefinementType typeType = new RefinementType(universeType, null);
    public static RefinementType boolType = new RefinementType(universeType, null);
    public static RefinementType unitType = new RefinementType(universeType, null);
    public static RefinementType intType = new RefinementType(universeType, null);
    public static ValueWrapper<Boolean> trueValue = new ValueWrapper<>(true, boolType);
    public static ValueWrapper<Boolean> falseValue = new ValueWrapper<>(false, boolType);
    public static Value unitValue = new BuiltinValue(unitType);

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
    public static Function equalsFunc = new DFAFunction(
            new BuiltinFunction(new FunctionType(boolType, universeType, universeType)) {
                @Override
                public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
                    return values[0].equals(values[1]) ? trueValue : falseValue;
                }
            }
    ) {
        @Override
        public DFA getDFA(int wrtArg, Value... inputs) {
            return DFAConditions.dfaEqualTo(inputs[0]);
        }
    };

    public static Function unequalsFunc = new DFAFunction(
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

    public static Type voidType = new RefinementType(universeType, new DFAFunction(
            new BuiltinFunction(new FunctionType(boolType, universeType)) {
                @Override
                public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
                    return falseValue;
                }
            }
    ) {
        @Override
        public DFA getDFA(int wrtArg, Value... inputs) {
            return DFA.alwaysFalse();
        }
    });

    public static BuiltinFunction plusFunc = new BuiltinFunction(new FunctionType(intType, intType, intType)) {
        @Override
        public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
            assert values[0] instanceof ValueWrapper;
            assert values[1] instanceof ValueWrapper;

            return new ValueWrapper<>(((ValueWrapper<Integer>) values[0]).wrappedValue + ((ValueWrapper<Integer>) values[1]).wrappedValue, intType);
        }
    };

    public static BuiltinFunction minusFunc = new BuiltinFunction(new FunctionType(intType, intType, intType)) {
        @Override
        public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
            assert values[0] instanceof ValueWrapper;
            assert values[1] instanceof ValueWrapper;

            return new ValueWrapper<>(((ValueWrapper<Integer>) values[0]).wrappedValue - ((ValueWrapper<Integer>) values[1]).wrappedValue, intType);
        }
    };

    public static BuiltinFunction multiplyFunc = new BuiltinFunction(new FunctionType(intType, intType, intType)) {
        @Override
        public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
            assert values[0] instanceof ValueWrapper;
            assert values[1] instanceof ValueWrapper;

            return new ValueWrapper<>(((ValueWrapper<Integer>) values[0]).wrappedValue * ((ValueWrapper<Integer>) values[1]).wrappedValue, intType);
        }
    };

    public static BuiltinFunction divideFunc = new BuiltinFunction(new FunctionType(intType, intType, intType)) {
        @Override
        public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
            assert values[0] instanceof ValueWrapper;
            assert values[1] instanceof ValueWrapper;

            return new ValueWrapper<>(((ValueWrapper<Integer>) values[0]).wrappedValue / ((ValueWrapper<Integer>) values[1]).wrappedValue, intType);
        }
    };

    public static BuiltinFunction negationFunc = new BuiltinFunction(new FunctionType(intType, intType)) {
        @Override
        public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
            assert values[0] instanceof ValueWrapper;

            return new ValueWrapper<>(-(Integer)((ValueWrapper<?>) values[0]).wrappedValue, intType);
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
        put("Void", voidType);
        put("Unit", unitType);
        put("unit", unitValue);
        put("!=", unequalsFunc);
        put("==", equalsFunc);
        put("+", plusFunc);
        put("-", minusFunc);
        put("/", divideFunc);
        put("*", multiplyFunc);
    }};


    static {
        boolType.condition = new DFAFunction(new BuiltinFunction(new FunctionType(boolType, universeType)) {
            @Override
            public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
                Value inputVal = values[0];
                return (inputVal == trueValue || inputVal == falseValue) ? trueValue : falseValue;
            }
        }) {
            @Override
            public DFA getDFA(int wrtArg, Value... inputs) {
                return DFAConditions.dfaEqualTo(trueValue).unionWith(DFAConditions.dfaEqualTo(falseValue));
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
                return DFAConditions.isInteger();
            }
        };

        nonzeroType.condition = new DFAFunction(
                new BuiltinFunction(new FunctionType(boolType, intType)
                ) {
                    @Override
                    public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
                        Value inputVal = values[0];
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
