package Parser;

import ErrorManager.ErrorManager;
import Lexer.Token;
import Lexer.TokenLibrary;
import Lexer.Tokenizer;
import Parser.Nonterminal.Definition;

import java.util.*;


public class Parser {
    private final ErrorManager errorManager;
    private final Nonterminal primaryNonterminal;
    private final ArrayList<Nonterminal> nonterminals;
    private final Tokenizer tokenizer;

    private final Stack<ParseTreeNode> valueStack = new Stack<>();
    private final Stack<ParsingState> controlStack = new Stack<>();
    private final HashMap<ParsingState, HashMap<Token, ParseDecision>> actionTable = new HashMap<>();

    public Parser(Nonterminal primaryNonterminal, ArrayList<Nonterminal> nonterminals, Tokenizer tokenizer, ErrorManager errorManager) {
        this.errorManager = errorManager;
        this.primaryNonterminal = primaryNonterminal;
        this.nonterminals =  nonterminals;
        this.tokenizer = tokenizer;
    }

    /**
     * buildParseTree
     *
     * Returns a parseTree based on the available rule library for given SymbolString
     * Returns null an logs an error if no such thing exists
     * */

    public ParseTreeNode buildParseTree() {
        makeFirstSets();
        makeFollowSets();


        /**
        while (true) {
            Symbol readSymbol = tokenizer.getSymbol();
            ParsingState topState = controlStack.peek();
            ParseDecision decision = actionTable.get(topState).get(readSymbol.getTokenType());

            if (decision instanceof ParseDecision.Shift) {
                ParsingState stateToPush = ((ParseDecision.Shift) decision).pushState;
                SymbolParseTreeNode nodeToPush = new SymbolParseTreeNode(readSymbol);

                valueStack.push(nodeToPush);
                controlStack.push(stateToPush);
            } else if (decision instanceof ParseDecision.Accept) {
                return valueStack.peek();
            } else if (decision instanceof ParseDecision.Reduce) {
                Definition someDefinition;
                ArrayList<ParseVariable> valueList = new ArrayList<ParseVariable>();
                for (int i = 0; i < someDefinition.getDefinitionString().size(); i++) {
                    valueList.add(valueStack.pop().asParseTreeNode());
                    controlStack.pop();
                }

                // x = Apply the semantic action to the popped elements of the value stack
                // Push x onto the value stack
                // Push the result of GOTO(currentState, R) onto the control stack
            }
         }

         */





        return new PDAParser(primaryNonterminal, errorManager).buildParseTree(tokenizer);
        //return primaryNonterminal.buildParseTree(inString, errorManager);
    }


    public ArrayList<Nonterminal.Definition> getDefinitions() {
        ArrayList<Nonterminal.Definition> retList = new ArrayList<>();

        for (Nonterminal nt : nonterminals) {
            retList.addAll(nt.getDefinitions());
        }

        return retList;
    }

    public void makeFollowSets() {

        ArrayList<Definition> definitions = getDefinitions();

        // Rule I
        primaryNonterminal.addToFollowSet(TokenLibrary.getEOF());

        // Rule IV
        for (Definition definition : definitions) {
            for (int i = 0; i<definition.getDefinitionString().size()-1; i++) {
                if (definition.getDefinitionString().get(i).isNonterminal()) {
                    definition.getDefinitionString().get(i).getNonterminal().addFirstMinusEpToFollow(definition.getDefinitionString().get(i+1).getFirstSet());
                }
            }
        }

        // Rule V

        for (Definition definition : definitions) {
            for (int i = 0; i<definition.getDefinitionString().size(); i++) {
                for (int k = i+1; k<definition.getDefinitionString().size(); k++) {
                    if (definition.getDefinitionString().get(i).isNonterminal() && definition.hasEpsBetween(i+1,k)) {
                        definition.getDefinitionString().get(i).getNonterminal().addFirstMinusEpToFollow(definition.getDefinitionString().get(k).getFirstSet());
                    }
                }
            }
        }

        boolean hasChanged = true;

        while (hasChanged) {
            hasChanged = false;

            // Rule II

            for (Definition definition : definitions) {
                if (definition.getDefinitionString().size() > 0) {
                    ParseVariable finalPV = definition.getDefinitionString().get(definition.getDefinitionString().size()-1);

                    if (finalPV.isNonterminal()) {
                        hasChanged |= finalPV.getNonterminal().addFollowSetToFollowSet(definition.getDefinedNT());
                    }
                }
            }

            // Rule III

            for (Definition definition : definitions) {
                if (definition.getDefinitionString().size() > 0) {
                    for (int i = 1; i<definition.getDefinitionString().size(); i++) {
                        ParseVariable finalPV = definition.getDefinitionString().get(i-1);

                        if (finalPV.isNonterminal() && definition.hasEpsBetween(i,definition.getDefinitionString().size())) {
                            hasChanged |= definition.getDefinedNT().addFollowSetToFollowSet(finalPV.getNonterminal());
                        }
                    }
                }
            }
        }
    }
    public void makeFirstSets() {
        ArrayList<Definition> definitions = getDefinitions();

        boolean hasChanged = true;

        while (hasChanged) {
            hasChanged = false;

            // Step III

            for (Definition definition : definitions) {
                if (definition.getDefinitionString().size() > 0) {
                    hasChanged |= definition.getDefinedNT().coallesceFirstSetMinusEp(definition.getDefinitionString().get(0).getFirstSet());
                } else {
                    hasChanged |= definition.getDefinedNT().addToFirstSet(TokenLibrary.getEpsilon());
                }
            }


            // Rule IV

            for (Definition definition : definitions) {
                for (int i = 0; i<definition.getDefinitionString().size(); i++) {
                    if (definition.hasLeadingEps(i)) {
                        hasChanged |= definition.getDefinedNT().coallesceFirstSetMinusEp(definition.getDefinitionString().get(i).getFirstSet());
                    }
                }
            }


            // Rule V

            for (Definition definition : definitions) {
                if (definition.hasLeadingEps(definition.getDefinitionString().size()-1)) {
                    hasChanged |= definition.getDefinedNT().addToFirstSet(TokenLibrary.getEpsilon());
                }
            }
        }

    }


    public void printFirstSets() {
        for (Nonterminal nonterminal : nonterminals) {
            StringBuilder setString = new StringBuilder();

            for (Token t : nonterminal.getFirstSet()) {
                setString.append(t).append(", ");
            }

            System.out.println("First("+nonterminal + ") = "+setString);
        }
    }

    public void printFollowSets() {
        for (Nonterminal nonterminal : nonterminals) {
            StringBuilder setString = new StringBuilder();

            for (Token t : nonterminal.getFollowSet()) {
                setString.append(t).append(", ");
            }

            System.out.println("Follow("+nonterminal + ") = "+setString);
        }
    }

    static class ParsingState {
        HashMap<Definition,Integer> positions;
        ParseTreeNode parseTree;

        ParsingState() {
            positions = new HashMap<>();
        }

        ParseTreeNode getParseTree() {
            return parseTree;
        }
        void advancePositions() {
            for (Map.Entry<Definition, Integer> position : positions.entrySet()) {
                ParseVariable locus = position.getKey().getDefinitionString().get(position.getValue());

            }
        }
    }
}
