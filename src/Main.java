import Elements.Value;
import Elements.ValueLibrary;
import Elements.ValueWrapper;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Interpreter.*;
import Regularity.DFA;
import Regularity.DFAConverter;
import Regularity.DFANode;
import Testing.TestSuite;

import java.io.File;
import java.util.ArrayList;


public class Main {
    public static void main(String[] args) {

        DFANode trueNodeA = new DFANode("T1", ValueLibrary.trueValue, null, null);
        trueNodeA.falseNode = trueNodeA;

        DFANode falseNodeA = new DFANode("F1", ValueLibrary.falseValue, null, null);

        DFANode trueNodeB = new DFANode("T2", ValueLibrary.trueValue, trueNodeA, null);
        trueNodeB.falseNode = trueNodeB;
        trueNodeA.trueNode = trueNodeB;
        DFANode falseNodeB = new DFANode("F2", ValueLibrary.falseValue, null, null);

        DFANode branchA = new DFANode("B1", ValueLibrary.trueValue, trueNodeA, trueNodeB);
        DFANode branchB = new DFANode("B2", ValueLibrary.falseValue, falseNodeA, falseNodeB);

        DFANode head = new DFANode("H", null, branchA, branchB);

        DFA myDFA = new DFA(head);

        System.out.println("All States: "+myDFA.getStates());

        DFA minimizedDFA = DFAConverter.minimizeDFA(myDFA);

        System.out.println("Minimized states: "+minimizedDFA.getStates());
        for (DFANode node : minimizedDFA.getStates()) {
            System.out.println(node+" "+node.trueNode+" "+node.falseNode);
        }




        // TestInterpretationSession sesh = new TestInterpretationSession("let a = {Int b -> b}");
        // OutputBuffer buff = sesh.testGetInterpretation();
        // System.out.println(buff);

        // TestSuite testSuite = new TestSuite(new File("tests"));

        // testSuite.getResults();


        /*Expression expr = new ReturnableExpressionSeries(ValueLibrary.boolType, new ArrayList<>() {{
            add(new ReturnExpression(new IdentityExpression(new ValueWrapper<>(true, ValueLibrary.universeType))));
        }});*/


        // ValidationContext v = expr.validate(new ValidationContext());
        // System.out.println(v.errors.get(0));


    }
}