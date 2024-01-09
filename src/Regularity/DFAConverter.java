package Regularity;

import Elements.Value;
import Elements.ValueLibrary;
import Interpreter.Expression;
import Interpreter.ExpressionContainer;
import Interpreter.ExpressionSeries;
import Interpreter.ReturnExpression;
import Utils.Result;

import java.util.*;

public class DFAConverter {
    // A DFA that returns true, always
    static DFA trueDFA;

    static {
        DFANode trueNode = new DFANode("accept", ValueLibrary.trueValue, null, null);
        trueNode.falseNode = trueNode;
        trueNode.trueNode = trueNode;

        trueDFA = new DFA(trueNode);
    }

    public static DFA dfaFrom(Expression ex) {

        // TODO: Figure out where to simplify

        HashMap<ReturnExpression, ArrayList<DFA>> returnClauses = new HashMap<>();

        /*
         Perform a DFS of the expression tree to search for return nodes
         For each node, record the path of conditions (expressed as DFAs) required to return that value
        */

        returnClauses = getReturnClauses(ex);

        ArrayList<DFA> intersectedDFAs = new ArrayList<>();

        for (Map.Entry<ReturnExpression, ArrayList<DFA>> returnConditions : returnClauses.entrySet()) {

            ReturnExpression retExpression = returnConditions.getKey();
            ArrayList<DFA> conditions = returnConditions.getValue();

            DFA retDFA = trueDFA;

            // Intersect all the relevant conditions
            for (DFA condition : conditions) {
                retDFA = retDFA.intersectionWith(condition);

                // Simplify at this step?
            }

            // Get return expression
            Expression returnedExpression = retExpression.getExprToReturn();

            // Check if ret expression can be reduced to value statically

            Result<Value, Exception> result = returnedExpression.reduceToValue();

            if (result.isOK()) {
                Value returnValue = result.getOkValue();

                // Replace 'trues' with the actual return value

                retDFA.replaceValue(ValueLibrary.trueValue, returnValue);

                intersectedDFAs.add(retDFA);
            } else {
                // TODO: Return some kind of error that it DFA conversion is not possible, with details
            }
        }

        // Unionize all the return values

        DFA unionizedDFA = intersectedDFAs.remove(0);

        for (DFA condition : intersectedDFAs) {
            unionizedDFA = unionizedDFA.unionWith(condition);

            // Simplify at this step?
        }

        // Simplify after everything's been done?

        return unionizedDFA;
    }

    private static HashMap<ReturnExpression, ArrayList<DFA>> getReturnClauses(Expression root) {
        // Start with the expression as root

        // Create set of previously discovered nodes
        HashSet<Expression> discovered = new HashSet<>();

        HashMap<ReturnExpression, ArrayList<DFA>> retMap = new HashMap<>();

        // Make root the first observed node
        ArrayList<ExpressionDFSNode> frontier = new ArrayList<>() {{
            add(new ExpressionDFSNode(root, new ArrayList<>()));
        }};

        while (frontier.size() > 0) {
            // For each observed node:
            ExpressionDFSNode currentNode = frontier.remove(0);
            Expression observed = currentNode.expression;
            ArrayList<DFA> conditions = new ArrayList<>();

            // TODO: Check if the observed node is an invalid type of expression, ie. a loop

            // Add to the frontier any internal expressions that satisfy any of the following:
            //  - They are conditionals
            //  - They are returnExpressions
            //  - They could contain returnExpressions that dictate the return value of this expression
            if (observed instanceof ExpressionContainer) {
                ArrayList<Expression> containedExpressions = ((ExpressionContainer) observed).getContainedExpressions();

                for (Expression containedExpression : containedExpressions) {
                    if (!discovered.contains(containedExpression) && containedExpression instanceof ReturnExpression) {
                        frontier.add(0, new ExpressionDFSNode(containedExpression, conditions));
                    }
                    if (!discovered.contains(containedExpression) && containedExpression instanceof ExpressionSeries) {
                        frontier.add(0, new ExpressionDFSNode(containedExpression, conditions));
                    }
                    // TODO: check for conditionals
                }

                discovered.addAll(containedExpressions);
            }

            // Check if it is a returnExpression
            if (observed instanceof ReturnExpression) {
                // If so, trace the list of conditionals that lead to it being run
                // Append this to the return hashmap

                retMap.put((ReturnExpression) observed, conditions);
            }
        }

        return retMap;
    }

    static class ExpressionDFSNode {
        Expression expression;
        ArrayList<DFA> conditions;
        public ExpressionDFSNode(Expression expression, ArrayList<DFA> conditions) {
            this.expression = expression;
            this.conditions = conditions;
        }
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
        // System.out.println(replacementSets);

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
