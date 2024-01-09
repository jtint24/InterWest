import Elements.Value;
import Elements.ValueLibrary;
import Elements.ValueWrapper;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Interpreter.*;
import Regularity.DFA;
import Regularity.DFAConditions;
import Regularity.DFAConverter;
import Regularity.DFANode;
import Testing.TestSuite;

import java.io.File;
import java.util.ArrayList;
import java.util.BitSet;


public class Main {
    public static void main(String[] args) {
        Value five = new ValueWrapper<>(5, ValueLibrary.intType);
        Value otherFive = new ValueWrapper<>(5, ValueLibrary.boolType);
        Value seven = new ValueWrapper<>(7, ValueLibrary.intType);
        Value two = new ValueWrapper<>(2, ValueLibrary.intType);

        DFA simpleDFA = DFAConditions.dfaEqualTo(five);
        System.out.println(simpleDFA);
        System.out.println(simpleDFA.getResult(five.toBoolString(), new ErrorManager(new OutputBuffer())));
        System.out.println(simpleDFA.getResult(otherFive.toBoolString(), new ErrorManager(new OutputBuffer())));
        System.out.println(simpleDFA.getResult(seven.toBoolString(), new ErrorManager(new OutputBuffer())));
        System.out.println(simpleDFA.getResult(two.toBoolString(), new ErrorManager(new OutputBuffer())));




        // Program that just returns true
        ExpressionSeries program = new ExpressionSeries(new ArrayList<>() {{
            add(new ReturnExpression(new IdentityExpression(ValueLibrary.falseValue)));
        }});

        DFA convertedDFA = DFAConverter.dfaFrom(program);

        System.out.println(convertedDFA);
    }


    public static void testDFAs(String[] args) {
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
        System.out.println(minimizedDFA);

        System.out.println("\nProduct test:");

        DFANode prod1Fail = new DFANode("AF", ValueLibrary.falseValue, null, null);
        prod1Fail.trueNode = prod1Fail;
        prod1Fail.falseNode = prod1Fail;

        DFANode prod1Head = new DFANode("AH", ValueLibrary.trueValue, prod1Fail, prod1Fail);
        prod1Head.trueNode = new DFANode("A2", ValueLibrary.falseValue, prod1Fail, prod1Head);

        DFANode prod2Fail = new DFANode("BF", ValueLibrary.falseValue, null, null);
        prod2Fail.trueNode = prod2Fail;
        prod2Fail.falseNode = prod2Fail;

        DFANode prod2Head = new DFANode("BH", ValueLibrary.trueValue, prod2Fail, prod2Fail);
        prod2Head.trueNode = new DFANode("B2", ValueLibrary.falseValue, prod2Fail, prod2Fail);
        prod2Head.trueNode.trueNode = new DFANode("B3", ValueLibrary.falseValue, prod2Fail, prod2Fail);


        DFA prod1DFA = new DFA(prod1Head);

        DFA prod2DFA = new DFA(prod2Head);

        System.out.println(prod1DFA.getStates());
        System.out.println(prod2DFA.getStates());


        DFA unionDFA = prod1DFA.unionWith(prod2DFA);

        System.out.println(unionDFA.getStates());

        System.out.println(unionDFA);

        DFA minimizedUnion = DFAConverter.minimizeDFA(unionDFA);

        System.out.println("...");

        System.out.println(minimizedUnion);

        System.out.println(DFAConverter.minimizeDFA(minimizedUnion));






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