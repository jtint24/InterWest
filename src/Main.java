import Elements.Value;
import Elements.ValueLibrary;
import Elements.ValueWrapper;
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

        // TestSuite testSuite = new TestSuite(new File("tests"));

        // testSuite.getResults();

        Expression expr = new ReturnableExpressionSeries(ValueLibrary.boolType, new ArrayList<>() {{
            add(new ReturnExpression(new IdentityExpression(new ValueWrapper<>(true, ValueLibrary.universeType))));
        }});


        ValidationContext v = expr.validate(new ValidationContext());
        System.out.println(v.errors.get(0));


    }
}