package Regularity;

import Elements.DFAFunction;
import ErrorManager.Error;
import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.ErrorLibrary;
import Interpreter.*;
import Parser.ParseTreeNode;
import Utils.Result;

import java.util.*;
import java.util.function.Function;

public class DFAConverter {
    // A DFA that returns true, always
    static DFA trueDFA = new DFA(new DFANode("accept", ValueLibrary.trueValue));

    public static Result<DFA, Function<ParseTreeNode, Error>> dfaFrom(Expression ex, String wrt) {

        // System.out.println("Getting DFA from "+ex);
        // System.out.println("underlying parse tree: "+ex.underlyingParseTree);

        // ex.initializeStaticValues(new StaticReductionContext());

        // TODO: Figure out where to simplify

        HashMap<ReturnExpression, DFA> returnClauses;

        /*
         Perform a DFS of the expression tree to search for return nodes
         For each node, record the path of conditions (expressed as DFAs) required to return that value
        */

        returnClauses = getReturnClauseConditions(ex, wrt).returnClauses;
        // System.out.println("Return clauses: "+returnClauses);


        ArrayList<DFA> intersectedDFAs = new ArrayList<>();

        for (Map.Entry<ReturnExpression, DFA> returnConditions : returnClauses.entrySet()) {

            ReturnExpression retExpression = returnConditions.getKey();
            DFA conditions = returnConditions.getValue();

            // Get return expression
            Expression returnedExpression = retExpression.getExprToReturn();

            // Check if ret expression can be reduced to value statically

            Result<Value, String> result = returnedExpression.reduceToValue();

            if (result.isOK()) {
                Value returnValue = result.getOkValue();

                // Replace 'trues' with the actual return value

                // System.out.print(conditions);
                // System.out.println(returnValue);

                conditions.replaceValue(ValueLibrary.trueValue, returnValue);
                // System.out.print(conditions);

                intersectedDFAs.add(conditions);
            } else {
                Result<DFA, List<Expression>> returnConversion = inlineExpressionToDFA(returnedExpression, wrt);
                if (returnConversion.isOK()) {
                    DFA returnDFA = returnConversion.getOkValue();

                    intersectedDFAs.add(conditions.intersectionWith(returnDFA));
                } else {
                    ArrayList<Expression> failedExpression = new ArrayList<>(returnConversion.getErrValue());
                    return Result.error((regularityDeclaration) -> ErrorLibrary.getFailedRegularityError(failedExpression, regularityDeclaration));
                }
            }
        }

        // Unionize all the return values


        DFA unionizedDFA = intersectedDFAs.remove(0);
        // "Unionized: ");
        // System.out.print(unionizedDFA);

        for (DFA condition : intersectedDFAs) {
            unionizedDFA = unionizedDFA.unionWith(condition);
            // System.out.println("Unionized: ");
            // System.out.print(unionizedDFA);

            // Simplify at this step?
        }

        // TODO: Intersect with the conditions of the type

        // Simplify after everything's been done?

        return Result.ok(unionizedDFA);
    }

    static Result<DFA, List<Expression>> inlineExpressionToDFA(Expression ex, String wrtIdentifier) {
        if (ex instanceof FunctionExpression) {
            FunctionExpression functionEx = (FunctionExpression) ex;
            Result<Value, String> functionResult = functionEx.getFuncExpression().staticValue;

            // System.out.println("Converting the expression: "+functionEx.getFuncExpression());
            // System.out.println("Static value: "+functionResult);
            // System.out.println(functionResult.getOkValue() instanceof DFAFunction);

            if (functionResult.isOK() && functionResult.getOkValue() instanceof DFAFunction) {
                // Ensure that all the arguments are statically known except for one which can be wrt variable
                ArrayList<Value> staticArguments = new ArrayList<>();
                ArrayList<Expression> errorArguments = new ArrayList<>();
                int wrtArgumentIdx = -1;

                ArrayList<Expression> argumentExpressions = functionEx.getArgumentExpressions();
                for (int i = 0; i < argumentExpressions.size(); i++) {
                    Expression argument = argumentExpressions.get(i);
                    // System.out.println(argument);
                    if (argument.staticValue.isOK()) {
                        staticArguments.add(argument.staticValue.getOkValue());
                    } else {
                        if (argument instanceof VariableExpression && ((VariableExpression) argument).getIdentifier().equals(wrtIdentifier)) {
                            if (wrtArgumentIdx == -1) {
                                wrtArgumentIdx = i;
                            } else {
                                errorArguments.add(argument);
                            }
                        } else {
                            errorArguments.add(argument);
                        }
                    }
                }

                if (errorArguments.size() > 0) {
                    return Result.error(errorArguments);
                }

                return Result.ok(((DFAFunction) functionResult.getOkValue()).getDFA(wrtArgumentIdx, staticArguments.toArray(new Value[0])));
            }
        }

        return Result.error(List.of(ex));
    }



    static class ReturnClauseConditionResult {
        public HashMap<ReturnExpression, DFA> returnClauses;
        public DFA passConditions; // The series of conditions that need to be met to get to this point
        // public boolean terminates = false;
        public ArrayList<Expression> unconvertableExpressions = new ArrayList<>();

        public ReturnClauseConditionResult(HashMap<ReturnExpression, DFA> returnClauses, DFA conditions) {
            this.returnClauses = returnClauses;
            this.passConditions = conditions;
        }
        public ReturnClauseConditionResult(HashMap<ReturnExpression, DFA> returnClauses, DFA conditions, ArrayList<Expression> unconvertableExpressions) {
            this.returnClauses = returnClauses;
            this.passConditions = conditions;
            this.unconvertableExpressions = unconvertableExpressions;
        }
/*
        public ReturnClauseConditionResult(HashMap<ReturnExpression, ArrayList<DFA>> returnClauses, ArrayList<DFA> conditions, boolean terminates) {
            this.returnClauses = returnClauses;
            this.conditions = conditions;
            this.terminates = terminates;
        }
 */
    }

    private static ReturnClauseConditionResult getReturnClauseConditions(Expression root, String wrt) {
        return getReturnClauseConditions(root,  DFA.alwaysTrue(), wrt);
    }

    private static ReturnClauseConditionResult getReturnClauseConditions(Expression root, DFA conditions, String wrt) {
        // System.out.println("Getting returns from \n"+root);
        HashMap<ReturnExpression, DFA> returnClauses = new HashMap<>();
        DFA passConditions = conditions;

        if (root instanceof ExpressionContainer) {
            // System.out.println("Expression container:");

            for (Expression ex : ((ExpressionContainer) root).getContainedExpressions()) {
                ReturnClauseConditionResult outResult;
                if (ex instanceof ConditionalExpression) {
                    Result<DFA, Function<ParseTreeNode, Error>> ifConditionResult = dfaFrom(((ConditionalExpression) ex).getCondition(), wrt);
                    if (!ifConditionResult.isOK()) {
                        ArrayList<Expression> unconvertableExpression = new ArrayList<>();
                        unconvertableExpression.add(ex);
                        return new ReturnClauseConditionResult(returnClauses, conditions, unconvertableExpression);
                    }
                    DFA ifCondition = ifConditionResult.getOkValue();

                    ExpressionSeries body = ((ConditionalExpression) ex).getBody();

                    DFA inConditions = passConditions.intersectionWith(ifCondition);

                    outResult = getReturnClauseConditions(body, inConditions, wrt);

                    // We can get to the other side of the if statement if EITHER
                    //  - We met the previous pass condition to get to the top of the if statement and didn't meet the if statement's condition
                    //  - We met the if statement's escape condition
                    // C = if statement condition P = previous pass condition E = escape if statement pass condition
                    // Our pass condition is: (P and not C) or E
                    // Which expression is faster? Probably the first one

                    ifCondition.invert();

                    DFA pAndNotC = passConditions.intersectionWith(ifCondition);


                    passConditions = pAndNotC.unionWith(outResult.passConditions);

                    // Add any collected return clauses:

                    returnClauses.putAll(outResult.returnClauses);

                } else {
                    outResult = getReturnClauseConditions(ex, passConditions, wrt);
                    passConditions = outResult.passConditions;
                    returnClauses.putAll(outResult.returnClauses);
                }
            }

        } else if (root instanceof ReturnExpression) {
            // System.out.println("Return expression:");

            returnClauses.put((ReturnExpression) root, conditions);

            return new ReturnClauseConditionResult(returnClauses, DFA.alwaysFalse());
        }

        return new ReturnClauseConditionResult(returnClauses, passConditions);
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
