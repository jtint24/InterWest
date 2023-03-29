package Parser;

import Lexer.Token;
import Lexer.TokenLibrary;

import java.util.ArrayList;

public class ParseRuleLibrary {

    private final Nonterminal plusNonterminal = new Nonterminal("plusNonterminal");




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
        ArrayList<Nonterminal> nonterminals = new ArrayList<>();
        nonterminals.add(plusNonterminal);
        return nonterminals;
    }
}
