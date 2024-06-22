package Elements;

import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Interpreter.Expression;
import Interpreter.ExpressionResult;
import Interpreter.State;
import Regularity.DFA;
import Utils.Result;
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

        if (superType instanceof UniverseType || this.equals(superType)) {
            return TriValue.TRUE;
        }

        TriValue parentIsSubtype = this.superType.subtypeOf(superType);

        if (parentIsSubtype == TriValue.TRUE) {
            return TriValue.TRUE;
        }

        Result<DFA, String> myDFAResult = getDFA();
        Result<DFA, String> superDFAResult = superType.getDFA();


        if (myDFAResult.isOK() && superDFAResult.isOK()) {


            DFA myDFA = myDFAResult.getOkValue();
            DFA superDFA = superDFAResult.getOkValue();

            // IF I can turn superType into a DFA and I can turn MYSELF into a DFA, then do that

            // Then compare superType's DFA to mine and see if it's a superset language

            TriValue dfaIsSubset = TriValue.fromBool(
                    myDFA.subsetLanguageOf(superDFA)
            );

            return dfaIsSubset;
        } else {
            // If I can't do the conversion, then I don't know if I'm a subtype at all!
            String mdrString = myDFAResult.toString().split("\n")[0];
            String sdrString = superDFAResult.toString().split("\n")[0];
            System.out.println("The DFA results were mine "+mdrString+" and others "+sdrString);
            return TriValue.UNKNOWN;
        }
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        if (v == null) {
            throw new IllegalArgumentException();
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

    @Override
    public Result<DFA, String> getDFA() {
        Result<DFA,String> superTypeDFA = superType.getDFA();
        if (superTypeDFA.isOK()) {
            if (condition instanceof DFAFunction) {
                DFA myDFA = ((DFAFunction) condition).getDFA(0);
                return Result.ok(((DFA)myDFA.clone()).intersectionWith((DFA)superTypeDFA.getOkValue().clone()));
             } else if (condition instanceof ExpressionFunction && ((ExpressionFunction) condition).getIsRegular()) {
                 DFA myDFA = ((ExpressionFunction) condition).getDFA();
                 return Result.ok(((DFA)myDFA.clone()).intersectionWith((DFA)superTypeDFA.getOkValue().clone()));
            } else {
                return Result.error("The type "+this+" doesn't have a condition that can be turned into a DFA.");
            }
        } else {
            return superTypeDFA;
        }
    }


}
