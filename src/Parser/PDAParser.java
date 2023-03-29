package Parser;


import Lexer.Token;
import Lexer.Tokenizer;
import Lexer.Symbol;
import ErrorManager.ErrorManager;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Stack;

/**
 * PDAParser
 *
 * Experimental parsing solution that simulates a PDA
 * */
public class PDAParser {
    HashSet<PDAState> pdaStates = new HashSet<>();

    private final Nonterminal primaryNonterminal;
    PDAParser(Nonterminal primaryNonterminal, ErrorManager errorManager) {
        this.primaryNonterminal = primaryNonterminal;
    }
    public ParseTreeNode buildParseTree(Tokenizer tokenizer) {

        pdaStates.add(new PDAState(primaryNonterminal));

        while (!tokenizer.isTerminated()) {
            Symbol newSymbol = tokenizer.getSymbol();
            System.out.println(newSymbol+" "+pdaStates.size());
            advanceFrame(newSymbol);
        }

        if (pdaStates.size() > 1) {
            // Uh oh! Ambiguity!
            return null;
        } else if (pdaStates.isEmpty()) {
            // Uh oh! No available parse!
            return null;
        } else {
            return ((PDAState) pdaStates.toArray()[0]).getTree();
        }
    }

    public void advanceFrame(Symbol newSymbol) {
        HashSet<PDAState> newPDAStates = new HashSet<>();

        Token newToken = newSymbol.getTokenType();
        for (PDAState state : pdaStates) {
            ParseVariable topVariable = state.getTopVariable();
            if (topVariable.isToken()) {
                if (newToken == topVariable.getToken()) {
                    state.popVariable();
                    newPDAStates.add(state);
                }
            } else {
                for (Nonterminal.Definition definition : topVariable.getNonterminal().getDefinitions()) {
                    newPDAStates.add(state.copyWithPushedDefinition(definition));
                }
            }
        }

        pdaStates = newPDAStates;
    }

    static class PDAState {
        Stack<ParseVariable> parseVariableStack;
        ParseTreeNode tree;

        PDAState(Nonterminal nt) {
            tree = new NonterminalParseTreeNode(nt, null);
            parseVariableStack = new Stack<ParseVariable>();
            parseVariableStack.push(nt.asParseVariable());
        }

        PDAState(Stack<ParseVariable> parseVariableStack, ParseTreeNode tree) {
            this.parseVariableStack = parseVariableStack;
            this.tree = tree;
        }

        public ParseTreeNode getTree() {
            return tree;
        }

        public ParseVariable getTopVariable() {
            return parseVariableStack.peek();
        }

        public void popVariable() {
            parseVariableStack.pop();
        }

        public PDAState copyWithPushedDefinition(Nonterminal.Definition definition) {
            Stack<ParseVariable> newStack = parseVariableStack;

            for (int i = definition.getDefinitionString().size()-1; i>=0; i--) {
                newStack.push(definition.getDefinitionString().get(i));
            }

            return new PDAState(newStack, tree);
        }
    }
}
