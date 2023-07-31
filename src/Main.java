import Interpreter.InterpretationSession;
import Testing.TestSuite;

import java.io.File;


public class Main {
    public static void main(String[] args) {

        // InterpretationSession newSession = new InterpretationSession("let func = a(10)");
        // InterpretationSession newSession = new InterpretationSession("let func = res(10)(30, \"abcs\", bob)(abced, a(c))");
        // newSession.runSession();

        TestSuite testSuite = new TestSuite(new File("tests"));

        testSuite.getResults();


    }
}