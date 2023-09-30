import IO.OutputBuffer;
import Interpreter.InterpretationSession;
import Testing.TestSuite;

import java.io.File;


public class Main {
    public static void main(String[] args) {

        InterpretationSession newSession = new InterpretationSession("1+2+3+4+5");
        OutputBuffer buffer = newSession.testBinaryExpressions();
        // System.out.println(buffer.toString());


        TestSuite testSuite = new TestSuite(new File("tests"));

        //testSuite.getResults();


    }
}