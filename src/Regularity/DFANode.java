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
    String name;

    public DFANode(String name, Value returnValue, DFANode trueNode, DFANode falseNode) {
        this.returnValue = returnValue;
        this.trueNode = trueNode;
        this.falseNode = falseNode;
        this.name = name;
    }

    public DFANode(String name, Value returnValue) {
        this(name, returnValue, null, null);
        this.trueNode = this;
        this.falseNode = this;
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

    public DFANode getSuccessor(boolean b) {
        if (b) {
            return trueNode;
        } else {
            return falseNode;
        }
    }

    public String toString() {
        return "{"+name+" "+returnValue+"}";
    }

    public DFANodeData getData() {
        return new DFANodeData(returnValue);
    }

    public void setSuccessor(Value symbol, DFANode successor) {
        if (symbol == ValueLibrary.trueValue) {
            trueNode = successor;
        } else if (symbol == ValueLibrary.falseValue) {
            falseNode = successor;
        } else {
            // TODO: Add error handling
        }
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
