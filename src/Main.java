import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Interpreter.*;
import Testing.TestSuite;

import java.io.File;


public class Main {
    public static void main(String[] args) {


        LetExpression myLet = new LetExpression(
                "my_variable",
                new IdentityExpression(ValueLibrary.falseValue)
        );

        OutputBuffer outputBuffer = new OutputBuffer();
        ErrorManager errorManager = new ErrorManager(outputBuffer);
        State myState = new State(errorManager);
        myState.addScope();

        ExpressionResult result = myLet.evaluate(myState);

        System.out.println(result.resultingState);
        System.out.println(result.resultingValue);




        // TestSuite testSuite = new TestSuite(new File("tests"));

        // testSuite.getResults();


    }
}