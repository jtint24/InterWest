package Elements;

import ErrorManager.ErrorManager;
import Interpreter.Expression;
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

            DFA superDFA = ((DFAFunction) ((RefinementType) superType).condition).getDFA(0);

            // Then compare superType's DFA to mine and see if it's a superset language

            TriValue dfaIsSubset = TriValue.fromBool(
                    myDFA.subsetLanguageOf(superDFA)
            );

            return dfaIsSubset;
        } else {
            // If I can't do the conversion, then I don't know if I'm a subtype at all!

            return TriValue.UNKNOWN;
        }
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        if (v == null) {
            var a =1;
            throw new RuntimeException();
        }

        if (superType.matchesValue(v, errorManager)) {

            DFA dfa;
            if (condition instanceof ExpressionFunction) {
                dfa = ((ExpressionFunction) condition).equivalentDFA;
            } else {
                dfa = ((DFAFunction) condition).getDFA(0);
            }
            Value result = dfa.getResult(v.toBoolString(), errorManager);

            return result.equals(ValueLibrary.trueValue);
        } else {
            return false;
        }
    }
}
