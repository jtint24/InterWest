package Regularity;

import Elements.Function;
import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Interpreter.State;

import java.util.ArrayList;
import java.util.HashMap;

public class DFANode {
    Value returnValue;
    public DFANode trueNode;
    public DFANode falseNode;
    public Function translationFunction;

    String name;

    public DFANode(String name, Value returnValue, DFANode trueNode, DFANode falseNode) {
        this.returnValue = returnValue;
        this.trueNode = trueNode;
        this.falseNode = falseNode;
        this.name = name;
    }

    public DFANode getSuccessor(Value v) {
        ErrorManager er = new ErrorManager(new OutputBuffer());
        v = translationFunction.apply(er, new State(er), v);
        return getDirectSuccessor(v);
    }

    public DFANode getDirectSuccessor(Value v) {
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
