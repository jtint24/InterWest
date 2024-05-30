package Regularity;

import Elements.Value;
import Elements.ValueLibrary;
import Elements.ValueWrapper;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;

import java.util.BitSet;
import java.util.function.Function;

public class DFAConditions {

    /**
     * Returns a DFA that accepts on an input that's inequal to v
     */
    public static DFA dfaInequalTo(Value v) {
        DFA equalDFA = dfaEqualTo(v);
        equalDFA.invert();
        return equalDFA;
    }

    public static DFA isInteger() {
        String prefix = "class java.lang.Integer\u001F";
        byte[] bytes = prefix.getBytes();

        return dfaPrefixedBy(BitSet.valueOf(bytes));
    }

    static void printBS(BitSet bs) {
        for (int i = 0; i<bs.size(); i++) {
            System.out.print(bs.get(i) ? 1 : 0);
        }
        System.out.println();
        for (byte b : bs.toByteArray()) {
            System.out.print((char) b);
        }
        System.out.println();
    }


    public static DFA dfaPrefixedBy(BitSet vBoolString) {

        DFANode rejectNode = new DFANode("reject", ValueLibrary.falseValue);

        // if (vBoolString.size() == 0) {
        //return new DFA(new DFANode("accept", ValueLibrary.trueValue, rejectNode, rejectNode));
        // }

        DFANode head = new DFANode("head", ValueLibrary.falseValue, null, null);
        DFANode pointer = head;

        for (int i = 0; i<vBoolString.size(); i++) {
            DFANode newPointer = new DFANode("node_"+i, ValueLibrary.falseValue, null, null);
            if (vBoolString.get(i)) {
                pointer.trueNode = newPointer;
                pointer.falseNode = rejectNode;
            } else {
                pointer.falseNode = newPointer;
                pointer.trueNode = rejectNode;
            }
            pointer = newPointer;
        }

        pointer.returnValue = ValueLibrary.trueValue;
        pointer.trueNode = pointer;
        pointer.falseNode = pointer;

        return new DFA(head);
    }

    /**
     * Returns on a DFA that accepts on an input that's equal to v
     * */
    public static DFA dfaEqualTo(Value v) {

        BitSet vBoolString = v.toBoolString();

        DFANode rejectNode = new DFANode("reject", ValueLibrary.falseValue);

        // if (vBoolString.size() == 0) {
            //return new DFA(new DFANode("accept", ValueLibrary.trueValue, rejectNode, rejectNode));
        // }

        DFANode head = new DFANode("head", ValueLibrary.falseValue, null, null);
        DFANode pointer = head;

        for (int i = 0; i<vBoolString.size(); i++) {
            DFANode newPointer = new DFANode("node_"+i, ValueLibrary.falseValue, null, null);
            if (vBoolString.get(i)) {
                pointer.trueNode = newPointer;
                pointer.falseNode = rejectNode;
            } else {
                pointer.falseNode = newPointer;
                pointer.trueNode = rejectNode;
            }
            pointer = newPointer;
        }

        pointer.returnValue = ValueLibrary.trueValue;
        pointer.trueNode = rejectNode;
        pointer.falseNode = rejectNode;

        return new DFA(head);
    }
}
