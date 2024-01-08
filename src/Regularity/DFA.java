package Regularity;

import Elements.Value;
import Elements.ValueLibrary;
import Elements.ValueWrapper;
import ErrorManager.ErrorManager;

import java.util.*;

public class DFA {
    DFANode startNode;
    HashSet<DFANode> states;
    static HashSet<Value> alphabet = new HashSet<>() {{
        add(ValueLibrary.trueValue);
        add(ValueLibrary.falseValue);
    }};

    public DFA(DFANode startNode) {
        this.startNode = startNode;
        this.states = getStatesFromStart(startNode);
    }

    public Value getResultFor(ArrayList<Value> values, ErrorManager er) {
        // TODO: make it work for a list of T/F values, not
        DFANode currentNode = startNode;

        for (Value value : values) {
            currentNode = currentNode.getSuccessor(value);

            if (currentNode == null) {
                return null;
            }
        }

        return currentNode.returnValue;
    }

    public Value getResultForValueString(ArrayList<Value> valueList, ErrorManager er) {
        DFANode currentNode = startNode;

        for (Value v : valueList) {
            currentNode = currentNode.getSuccessor(v);
        }

        return currentNode.returnValue;
    }

    /**
     * nodesWhereSymbolLeadsToSet
     *
     * For an alphabet symbol c and a set of nodes A, returns nodes where a transition on c leads to a node in a
     * */
    public HashSet<DFANode> nodesWhereSymbolLeadsToSet(Value symbol, HashSet<DFANode> a) {

        // System.out.println("States that go to "+a+" on "+symbol);

        HashSet<DFANode> retSet = new HashSet<>();
        HashSet<DFANode> frontier = new HashSet<>();
        frontier.add(startNode);
        HashSet<DFANode> exploredNodes = new HashSet<>();

        while (!frontier.isEmpty()) {
            DFANode nextNode = frontier.iterator().next();
            frontier.remove(nextNode);
            exploredNodes.add(nextNode);

            for (Value nextSymbol : alphabet) {
                DFANode successor = nextNode.getSuccessor(nextSymbol);
                if (successor != null && !exploredNodes.contains(successor)) {
                    frontier.add(successor);
                    exploredNodes.add(nextNode);
                }

                if (symbol == nextSymbol && a.contains(successor)) {
                    retSet.add(nextNode);
                }
            }
        }

        // System.out.println(" > "+retSet);
        return retSet;
    }

    public HashSet<DFANode> getStates() {
        return states;
    }

    private static HashSet<DFANode> getStatesFromStart(DFANode startNode) {
        HashSet<DFANode> frontier = new HashSet<>();
        frontier.add(startNode);
        HashSet<DFANode> exploredNodes = new HashSet<>();

        while (!frontier.isEmpty()) {
            DFANode nextNode = frontier.iterator().next();
            frontier.remove(nextNode);
            exploredNodes.add(nextNode);

            for (Value nextSymbol : alphabet) {
                DFANode successor = nextNode.getSuccessor(nextSymbol);
                if (successor != null && !exploredNodes.contains(successor)) {
                    frontier.add(successor);
                    exploredNodes.add(nextNode);
                }
            }
        }
        return exploredNodes;
    }

    /**
     * getAcceptStates
     *
     * Returns a hashmap of all the possible accept/return values mapping to all the states that return that value
     *
     * For example, if the states are {A, B, C, D, E}, where A, B, and D all return 0, and C and E return 1, the result
     * is: {0: {A, B, D}, 1: {C, E}}
     * */
    public HashMap<Value, HashSet<DFANode>> getAcceptStateSets() {
        HashSet<DFANode> allStates = getStates();
        HashMap<Value, HashSet<DFANode>> retMap = new HashMap<>();
        for (DFANode state : allStates) {
            if (!retMap.containsKey(state.returnValue)) {
                retMap.put(state.returnValue, new HashSet<>());
            }
            retMap.get(state.returnValue).add(state);
        }

        return retMap;
    }


    /**
     * getEquivalentNodes
     *
     * Returns a set of all sets of nodes which are equivalent to one another in terms of function and return type
     * */
    public HashMap<DFANode.DFANodeData, HashSet<DFANode>> getEquivalentNodes() {
        HashSet<DFANode> allStates = getStates();
        HashMap<DFANode.DFANodeData, HashSet<DFANode>> retMap = new HashMap<>();
        for (DFANode state : allStates) {
            if (!retMap.containsKey(state.getData())) {
                retMap.put(state.getData(), new HashSet<>());
            }
            retMap.get(state.getData()).add(state);
        }

        return retMap;
    }

    // TODO: Add intersection and union


    public DFA unionWith(DFA otherDFA) {
        HashMap<DFANode, HashMap<DFANode, DFANode>> productMapping = new HashMap<>();

        // Populate the product mapping

        DFANode myFailState = new DFANode("Fail", ValueLibrary.falseValue, null, null);
        HashSet<DFANode> myProdStates = new HashSet<>(getStates());
        myProdStates.add(myFailState);

        DFANode otherFailState = new DFANode("Fail", ValueLibrary.falseValue, null, null);
        HashSet<DFANode> otherProdStates = new HashSet<>(otherDFA.getStates());
        otherProdStates.add(otherFailState);

        for (DFANode myState : myProdStates) {

            productMapping.put(myState, new HashMap<>());

            // TODO: validate return value

            for (DFANode otherState : otherProdStates) {

                // TODO: validate return value

                Value returnValue;

                if (myState.returnValue == ValueLibrary.trueValue || otherState.returnValue == ValueLibrary.trueValue) {
                    returnValue = ValueLibrary.trueValue;
                } else {
                    returnValue = ValueLibrary.falseValue;
                }

                productMapping.get(myState).put(otherState, new DFANode(myState.name + "_"+otherState.name, returnValue, null, null));
            }
        }

        // productMapping.get(myFailState).put(otherFailState, null);

        // Calculate product state transitions
        return DFA.fromProductMapping(productMapping, startNode, otherDFA.startNode, myFailState, otherFailState);
    }


    public DFA intersectionWith(DFA otherDFA) {

        HashMap<DFANode, HashMap<DFANode, DFANode>> productMapping = new HashMap<>();

        // Populate the product mapping

        DFANode myFailState = new DFANode("Fail", ValueLibrary.falseValue, null, null);
        HashSet<DFANode> myProdStates = new HashSet<>(getStates());
        myProdStates.add(myFailState);

        DFANode otherFailState = new DFANode("Fail", ValueLibrary.falseValue, null, null);
        HashSet<DFANode> otherProdStates = new HashSet<>(otherDFA.getStates());
        otherProdStates.add(otherFailState);

        for (DFANode myState : myProdStates) {

            productMapping.put(myState, new HashMap<>());

            // TODO: validate return value

            for (DFANode otherState : otherProdStates) {

                // TODO: validate return value

                Value returnValue;

                if (myState.returnValue == ValueLibrary.trueValue && otherState.returnValue == ValueLibrary.trueValue) {
                    returnValue = ValueLibrary.trueValue;
                } else {
                    returnValue = ValueLibrary.falseValue;
                }

                productMapping.get(myState).put(otherState, new DFANode(myState.name + "_"+otherState.name, returnValue, null, null));
            }
        }

        // Calculate product state transitions

        return DFA.fromProductMapping(productMapping, startNode, otherDFA.startNode, myFailState, otherFailState);
    }

    public static DFA fromProductMapping(HashMap<DFANode, HashMap<DFANode, DFANode>> productMapping, DFANode startNode1, DFANode startNode2, DFANode failState1, DFANode failState2) {
        Set<DFANode> prodStates1 = productMapping.keySet();
        Set<DFANode> prodStates2 = productMapping.get(startNode1).keySet();

        for (DFANode myState : prodStates1) {
            for (DFANode otherState : prodStates2) {
                for (Value symbol : alphabet) {
                    DFANode mySuccessor = myState.getSuccessor(symbol) == null ? failState1 : myState.getSuccessor(symbol);
                    DFANode otherSuccessor = otherState.getSuccessor(symbol) == null ? failState2 : otherState.getSuccessor(symbol);
                    DFANode productSuccessor = productMapping.get(mySuccessor).get(otherSuccessor);
                    DFANode currentProduct = productMapping.get(myState).get(otherState);

                    if (currentProduct != null) {
                        currentProduct.setSuccessor(symbol, productSuccessor);
                    }
                }
            }
        }

        return new DFA(productMapping.get(startNode1).get(startNode2));
    }

    @Override
    public String toString() {
        StringBuilder retString = new StringBuilder();

        for (DFANode node : getStates()) {
            retString.append(node).append(" ").append(node.trueNode).append(" ").append(node.falseNode).append("\n");
        }

        return retString.toString();
    }


    /**
     * replaceValue
     *
     * Replaces all instances of value1 with value2 in the DFA's nodes' return values
     * */

    public void replaceValue(Value value1, Value value2) {
        for (DFANode node : getStates()) {
            if (node.returnValue == value1) {
                node.returnValue = value2;
            }
        }
    }
}
