package Regularity;

import Elements.Value;
import Interpreter.Expression;

import java.util.*;

public class DFAConverter {
    public static DFA dfaFrom(Expression ex) {
        return null;
    }

    public static DFA minimizeDFA(DFA dfa) {
        // Hopcroft's algorithm to remove non-distinguishable states


        HashSet<HashSet<DFANode>> refinementSets = new HashSet<>();
        HashSet<HashSet<DFANode>> replacementSets = new HashSet<>();

        for (HashSet<DFANode> acceptStates : dfa.getAcceptStateSets().values()) {
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
