package Parser;

import Lexer.TokenLibrary;
public class ParseRuleLibrary {

    public static Nonterminal getStartRule() {
        return plusNonterminal;
    }

    private static final Nonterminal plusNonterminal = new Nonterminal(
            new Nonterminal.Definition(TokenLibrary.intToken.asParseVariable(), TokenLibrary.plusToken.asParseVariable(), TokenLibrary.intToken.asParseVariable())
    );
}
