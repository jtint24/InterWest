import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Interpreter.*;
import Testing.TestSuite;

import java.io.File;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {


        InterpretationSession sesh = new InterpretationSession("let my_var = true\nreturn my_var");
        sesh.runSession();


        // TestSuite testSuite = new TestSuite(new File("tests"));

        // testSuite.getResults();


    }
}