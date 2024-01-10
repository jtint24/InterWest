package Elements;

import ErrorManager.ErrorManager;
import Regularity.DFA;
import Regularity.DFAConditions;

public class Operations {
    public static Function equalsValue(Value v) {
        FunctionType type = new FunctionType(ValueLibrary.boolType, ValueLibrary.universeType);

        Function coreFunc = new BuiltinFunction(type) {
            @Override
            public Value prevalidatedApply(ErrorManager errorManager, Value[] values) {
                return values[0] == v ? ValueLibrary.trueValue : ValueLibrary.falseValue;
            }
        };
        return new DFAFunction(coreFunc) {
            @Override
            public DFA getDFA(Value... inputs) {
                return DFAConditions.dfaEqualTo(inputs[0]);
            }
        };
    }
}
