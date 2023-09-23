import Testing.TestSuite;

import java.io.File;


public class Main {
    public static void main(String[] args) {

        // InterpretationSession newSession = new InterpretationSession("12 \"\"");
        // newSession.runSession();


        TestSuite testSuite = new TestSuite(new File("tests"));

        testSuite.getResults();


    }
}