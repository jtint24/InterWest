package Elements;

import ErrorManager.ErrorManager;
import Interpreter.ExpressionResult;
import Interpreter.State;
import Regularity.DFA;
import Utils.TriValue;

public class RefinementType extends Type {
    Type superType;

    Function condition;

    public RefinementType(Type superType, Function condition) {
        this.superType = superType;
        this.condition = condition;
    }

    @Override
    public TriValue subtypeOf(Type superType) {
        // System.out.println("Checking if "+this+" is a subtype of "+superType);

        if (superType instanceof TypeExpression) {
            if (((TypeExpression) superType).getStaticValue().isOK()) {
                return subtypeOf(((TypeExpression) superType).getStaticValue().getOkValue());
            } else {
                return TriValue.UNKNOWN;
            }
        }

        if (superType instanceof UniverseType || this == superType) {
            return TriValue.TRUE;
        }

        TriValue parentIsSubtype = this.superType.subtypeOf(superType);

        if (parentIsSubtype == TriValue.TRUE) {
            return TriValue.TRUE;
        }

        if (condition instanceof DFAFunction && superType instanceof RefinementType && ((RefinementType) superType).condition instanceof DFAFunction) {

            // IF I can turn superType into a DFA and I can turn MYSELF into a DFA, then do that
            DFA myDFA = ((DFAFunction) condition).getDFA(0);
            // System.out.println("myDFA:");
            // System.out.println(myDFA);

            DFA superDFA = ((DFAFunction) ((RefinementType) superType).condition).getDFA(0);
            // System.out.println("SuperDFA:");
            // System.out.println(superDFA);

            // Then compare superType's DFA to mine and see if it's a superset language

            TriValue dfaIsSubset = TriValue.fromBool(
                    myDFA.subsetLanguageOf(superDFA)
            );

            // System.out.println(dfaIsSubset);

            return dfaIsSubset;
        } else {
            // If I can't do the conversion, then I don't know if I'm a subtype at all!

            return TriValue.UNKNOWN;
        }
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        if (superType.matchesValue(v, errorManager)) {
            Value result = condition.apply(errorManager, v);

            return result.equals(ValueLibrary.trueValue);
        } else {
            return false;
        }
    }
}
