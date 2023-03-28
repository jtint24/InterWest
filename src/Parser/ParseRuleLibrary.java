package Parser;

import Lexer.TokenLibrary;

import java.util.ArrayList;

public class ParseRuleLibrary {

    private Nonterminal plusNonterminal = new Nonterminal("plusNonterminal");


    public ParseRuleLibrary() {
        this.plusNonterminal.addDefinitions(
                new Nonterminal.Definition(plusNonterminal, TokenLibrary.intToken.asParseVariable(), TokenLibrary.plusToken.asParseVariable(), TokenLibrary.intToken.asParseVariable()),
                new Nonterminal.Definition(plusNonterminal, plusNonterminal.asParseVariable(), TokenLibrary.plusToken.asParseVariable(), plusNonterminal.asParseVariable())
        );
    }

    public Nonterminal getStartRule() {
        return plusNonterminal;
    }


    public ArrayList<Nonterminal> getNonterminals() {
        ArrayList<Nonterminal> nonterminals = new ArrayList<Nonterminal>();
        nonterminals.add(plusNonterminal);
        return nonterminals;
    }
}
