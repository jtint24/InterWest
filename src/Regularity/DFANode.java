package Regularity;

import Elements.Value;
import Elements.ValueLibrary;

import java.util.HashMap;

public class DFANode {
    Value returnValue;
    public DFANode trueNode;
    public DFANode falseNode;

    String name;

    public DFANode(String name, Value returnValue, DFANode trueNode, DFANode falseNode) {
        this.returnValue = returnValue;
        this.trueNode = trueNode;
        this.falseNode = falseNode;
        this.name = name;
    }

    public DFANode getSuccessor(Value v) {
        if (v == ValueLibrary.trueValue) {
            return trueNode;
        } else if (v == ValueLibrary.falseValue) {
            return falseNode;
        } else {
            return null;
            // TODO: Add error handling
        }
    }

    public String toString() {
        return "{"+name+" "+returnValue+"}";
    }
}
