package Parser;

import ErrorManager.ErrorManager;
import Lexer.SymbolString;

public class Parser {

    private final ErrorManager errorManager;

    private final Nonterminal primaryNonterminal;

    public Parser(Nonterminal primaryNonterminal, ErrorManager errorManager) {
        this.errorManager = errorManager;
        this.primaryNonterminal = primaryNonterminal;
    }

    /**
     * buildParseTree
     *
     * Returns a parseTree based on the available rule library for given SymbolString
     * Returns null an logs an error if no such thing exists
     * */
    public ParseTreeNode buildParseTree(SymbolString inString) {
        return primaryNonterminal.buildParseTree(inString, errorManager);
    }
}
