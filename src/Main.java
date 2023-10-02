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


        // TestInterpretationSession sesh = new TestInterpretationSession("let my_var = let my_second_var = true");
        // OutputBuffer buff = sesh.testGetInterpretation();
        // System.out.println(buff);

        TestSuite testSuite = new TestSuite(new File("tests"));

        testSuite.getResults();


    }
}