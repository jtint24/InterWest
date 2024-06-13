package Main;

import Elements.Value;
import Elements.ValueLibrary;
import Elements.ValueWrapper;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Regularity.DFA;
import Regularity.DFAConditions;

public class PerformanceTests {
    public static void main(String[] args) {
        testDFASpeed();
    }

    public static void testDFASpeed() {
        DFA testDFA = DFAConditions.dfaEqualTo(new ValueWrapper<>(4, ValueLibrary.intType));
        Value testVal = new ValueWrapper<>(4, ValueLibrary.intType);

        long startTimeMillis = System.currentTimeMillis();
        for (int i = 0; i<10_000_000; i++) {
            testDFA.getResult(testVal.toBoolString(), new ErrorManager(new OutputBuffer()));
        }
        long endTimeMillis = System.currentTimeMillis();
        long duration = endTimeMillis - startTimeMillis;

        System.out.println("Total time: "+(duration)+" millis");
        System.out.println("Time/check: "+(((float)duration)/1000000_0.0));
        System.out.println("Time/dfaNode: "+(((float)duration)/1000000_0.0/((float)testVal.toBoolString().size())));
    }
}
