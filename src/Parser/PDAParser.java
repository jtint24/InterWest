package Parser;


import Lexer.Token;
import Lexer.TokenLibrary;
import Lexer.Tokenizer;
import Lexer.Symbol;
import ErrorManager.ErrorManager;

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
        // pdaStates.add(new PDAState(primaryNonterminal));

        while (true) {
            HashSet<PDAState> newPDAStates = new HashSet<>();

            Symbol newSymbol = tokenizer.getSymbol();

            for (PDAState state : pdaStates) {
                if (state.isEmpty()) {
                    return new SymbolParseTreeNode(new Symbol("hooray", TokenLibrary.plusToken));
                }
                HashSet<PDAState> stateCanContinue = state.getIterated(newSymbol);

                newPDAStates.addAll(stateCanContinue);
            }

        }

    }

    class PDAState implements Cloneable {
        Stack<ParseTreeNode> pdaStack = new Stack<>();

        public PDAState(Stack<ParseTreeNode> stack) {
            this.pdaStack = stack;
        }

        public PDAState() {}

        public HashSet<PDAState> getIterated(Symbol inputSymbol) {
            HashSet<PDAState> returnedPDAState = new HashSet<>();

            if (pdaStack.peek() instanceof SymbolParseTreeNode) {
                if (((SymbolParseTreeNode) pdaStack.peek()).getSymbol() == inputSymbol) {
                    pdaStack.pop();
                    returnedPDAState.add((PDAState) this.clone());
                }
                return returnedPDAState;
            } else {

            }

            return null;


        }

        public Object clone() {
            return new PDAState((Stack<ParseTreeNode>) pdaStack.clone());
        }

        public boolean isEmpty() {
            return false;
        }
    }
}
