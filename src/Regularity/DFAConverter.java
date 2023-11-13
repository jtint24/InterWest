package Regularity;

import Elements.Value;
import Interpreter.Expression;

import java.util.*;

public class DFAConverter {
    public static DFA dfaFrom(Expression ex) {

        // TODO:
        // Perform a DFS of the expression tree to search for return nodes
        // For each return node, calculate all the predicates that need to be satisfied to visit it
        // Create a DFA of all the intersection of all the predicates (also expressible as DFAs)
        // Create a union of all the intersections, return that

        return null;
    }

    public static boolean checkEquivalence(DFA dfa1, DFA dfa2) {
        DFA minimalDFA1 = minimizeDFA(dfa1);
        DFA minimalDFA2 = minimizeDFA(dfa2);

        return checkIdentical(minimalDFA1, minimalDFA2);
    }

    public static boolean checkIdentical(DFA baseDFA, DFA otherDFA) {
        HashMap<DFANode, DFANode> equivalentNodes = new HashMap<>();

        ArrayList<DFANode> baseFrontier = new ArrayList<>();
        ArrayList<DFANode> otherFrontier = new ArrayList<>();

        baseFrontier.add(baseDFA.startNode);
        otherFrontier.add(otherDFA.startNode);

        HashSet<DFANode> baseExploredNodes = new HashSet<>();
        HashSet<DFANode> otherExploredNodes = new HashSet<>();

        while (baseFrontier.size() != 0) {
            DFANode baseNode = baseFrontier.remove(0);
            DFANode otherNode = otherFrontier.remove(0);

            if (equivalentNodes.containsKey(baseNode)) {
                if (equivalentNodes.get(baseNode) != otherNode) {
                    return false;
                }
            } else {
                if (baseNode.getData().sameNodeTypeAs(otherNode.getData())) {
                    equivalentNodes.put(baseNode, otherNode);
                } else {
                    return false;
                }
            }


            for (Value symbol : DFA.alphabet) {
                DFANode advancedBaseNode = baseNode.getSuccessor(symbol);
                DFANode advancedOtherNode = otherNode.getSuccessor(symbol);

                if ((advancedBaseNode == null) != (advancedOtherNode == null)) {
                    return false;
                }

                if (advancedBaseNode != null && !otherExploredNodes.contains(otherNode) && !baseExploredNodes.contains(baseNode)) {
                    baseFrontier.add(advancedBaseNode);
                    otherFrontier.add(advancedOtherNode);
                }
            }

            baseExploredNodes.add(baseNode);
            otherExploredNodes.add(otherNode);
        }

        return true;
    }
    public static DFA minimizeDFA(DFA dfa) {
        // Hopcroft's algorithm to remove non-distinguishable states


        HashSet<HashSet<DFANode>> refinementSets = new HashSet<>();
        HashSet<HashSet<DFANode>> replacementSets = new HashSet<>();

        for (HashSet<DFANode> acceptStates : dfa.getEquivalentNodes().values()) {
            refinementSets.add(acceptStates);
            replacementSets.add(acceptStates);
        }

        while (!refinementSets.isEmpty()) {
            HashSet<DFANode> refinementSet = refinementSets.iterator().next();
            refinementSets.remove(refinementSet);

            for (Value symbol : DFA.alphabet) {
                HashSet<DFANode> leadingNodes = dfa.nodesWhereSymbolLeadsToSet(symbol, refinementSet);

                ArrayList<HashSet<DFANode>> pList = new ArrayList<>(replacementSets);

                for (int i = 0; i<pList.size(); i++) {
                    HashSet<DFANode> replacementSet = pList.get(i);
                    if (!replacementSets.contains(replacementSet)) {
                        continue;
                    }

                    HashSet<DFANode> intersection = new HashSet<>(leadingNodes);
                    intersection.retainAll(replacementSet);

                    HashSet<DFANode> difference = new HashSet<>(replacementSet);
                    difference.removeAll(leadingNodes);

                    if (intersection.size() == 0 || difference.size() == 0) {
                        continue;
                    }

                    replacementSets.remove(replacementSet);
                    replacementSets.add(intersection);
                    replacementSets.add(difference);
                    pList.add(intersection);
                    pList.add(difference);

                    if (refinementSets.contains(replacementSet)) {
                        refinementSets.remove(replacementSet);
                        refinementSets.add(intersection);
                        refinementSets.add(difference);
                    } else {
                        if (intersection.size() <= difference.size()) {
                            refinementSets.add(intersection);
                        } else {
                            refinementSets.add(difference);
                        }
                    }

                }
            }
        }
        System.out.println(replacementSets);

        // Create a HashMap for that maps every node to its replacement, based on the replacement set

        HashMap<DFANode, DFANode> replacements = new HashMap<>();
        HashSet<DFANode> nodesToRemove = new HashSet<>();

        for (HashSet<DFANode> replacementSet : replacementSets) {
            DFANode keyNode = replacementSet.iterator().next();

            for (DFANode node : replacementSet) {
                replacements.put(node, keyNode);
                if (node != keyNode) {
                    nodesToRemove.add(node);
                }
            }
        }

        // Remove redundant nodes

        dfa.states.removeAll(nodesToRemove);

        for (DFANode node : dfa.states) {
            node.falseNode = replacements.get(node.falseNode);
            node.trueNode = replacements.get(node.trueNode);
        }

        return dfa;
    }
}
