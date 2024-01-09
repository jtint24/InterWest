package Regularity;

import Elements.Value;
import Elements.ValueLibrary;

import java.util.BitSet;

public class DFAConditions {

    public static DFA dfaInequalTo(Value v) {
        DFA equalDFA = dfaEqualTo(v);
        equalDFA.invert();
        return equalDFA;
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
