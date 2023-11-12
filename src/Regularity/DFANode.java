package Regularity;

import Elements.Function;
import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Interpreter.State;

import java.util.Objects;

public class DFANode {
    Value returnValue;
    public DFANode trueNode;
    public DFANode falseNode;
    public Function translationFunction; // TODO: Remove this

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

    public DFANodeData getData() {
        return new DFANodeData(returnValue);
    }



    static class DFANodeData {
        Value returnValue;

        public DFANodeData(Value returnValue) {
            this.returnValue = returnValue;
        }

        @Override
        public boolean equals(Object obj) {
            if (obj instanceof DFANodeData) {
                return this.sameNodeTypeAs((DFANodeData) obj);
            } else {
                return false;
            }
        }

        public boolean sameNodeTypeAs(DFANodeData otherNode) {
            return otherNode.returnValue == returnValue;
        }

        @Override
        public int hashCode() {
            return Objects.hash(returnValue);
        }
    }
}
