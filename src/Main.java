import Interpreter.*;
import Testing.TestSuite;

import java.io.File;


public class Main {
    public static void main(String[] args) {
        InterpretationSession sesh = new InterpretationSession(
                "let a = 4 \n let b = a"
        );
        sesh.runSession();

        runTests();
    }

    public static void runTests() {
        TestSuite testSuite = new TestSuite(new File("tests"));

        testSuite.getResults();
    }
}