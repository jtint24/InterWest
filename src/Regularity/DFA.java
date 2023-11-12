package Regularity;

import Elements.Value;
import Elements.ValueLibrary;
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

    public Value getResultFor(Value v, ErrorManager er) {
        DFANode currentNode = startNode;

        while (true) {
            DFANode successor = currentNode.getDirectSuccessor(v);

            if (successor != null) {
                currentNode = successor;
            } else {
                break;
            }
        }

        return currentNode.returnValue;
    }

    public Value getResultForValueString(ArrayList<Value> valueList, ErrorManager er) {
        DFANode currentNode = startNode;

        for (Value v : valueList) {
            currentNode = currentNode.getDirectSuccessor(v);
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
                DFANode successor = nextNode.getDirectSuccessor(nextSymbol);
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
                DFANode successor = nextNode.getDirectSuccessor(nextSymbol);
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
}
