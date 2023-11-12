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

        DFANode trueNode = new DFANode("T", ValueLibrary.trueValue, null, null);
        DFANode falseNode = new DFANode("F", ValueLibrary.falseValue, null, null);

        DFANode collapseNode1 = new DFANode("C1", null, trueNode, falseNode);
        DFANode collapseNode2 = new DFANode("C2", null, trueNode, falseNode);
        DFANode collapseNode3 = new DFANode("C3", null, trueNode, falseNode);

        DFANode loopNode1 = new DFANode("L1", null, collapseNode3, null);
        DFANode loopNode2 = new DFANode("L2", null, collapseNode3, loopNode1);
        loopNode1.falseNode = loopNode2;



        DFANode head = new DFANode("H", null, loopNode2, collapseNode2);

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