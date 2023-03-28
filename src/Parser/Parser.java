package Parser;

import ErrorManager.ErrorManager;
import Lexer.SymbolString;
import Lexer.TokenLibrary;
import Parser.Nonterminal.Definition;

import java.util.ArrayList;


public class Parser {

    private final ErrorManager errorManager;

    private final Nonterminal primaryNonterminal;

    private final ArrayList<Nonterminal> nonterminals;

    public Parser(Nonterminal primaryNonterminal, ArrayList<Nonterminal> nonterminals, ErrorManager errorManager) {
        this.errorManager = errorManager;
        this.primaryNonterminal = primaryNonterminal;
        this.nonterminals =  nonterminals;
    }

    /**
     * buildParseTree
     *
     * Returns a parseTree based on the available rule library for given SymbolString
     * Returns null an logs an error if no such thing exists
     * */
    public ParseTreeNode buildParseTree(SymbolString inString) {


        return null;
        //return primaryNonterminal.buildParseTree(inString, errorManager);
    }


    public ArrayList<Nonterminal.Definition> getDefinitions() {
        ArrayList<Nonterminal.Definition> retList = new ArrayList<>();

        for (Nonterminal nt : nonterminals) {
            retList.addAll(nt.getDefinitions());
        }

        return retList;
    }
    public void makeFirstSet() {
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
                for (int i = 0; i<definition.getDefinitionString().size()-1; i++) {
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


}
