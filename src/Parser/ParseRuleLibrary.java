package Parser;

import Lexer.Token;
import Lexer.TokenLibrary;

import java.util.ArrayList;

public class ParseRuleLibrary {

    // private Nonterminal plusNonterminal = new Nonterminal("plusNonterminal");

    private final Nonterminal S = new Nonterminal("S");
    private final Nonterminal A = new Nonterminal("A");
    private final Nonterminal B = new Nonterminal("B");
    private final Nonterminal C = new Nonterminal("C");

    private final Nonterminal D = new Nonterminal("D");
    private final Nonterminal E = new Nonterminal("E");
    private final Nonterminal F = new Nonterminal("F");



    public ParseRuleLibrary() {
        /*
        this.plusNonterminal.addDefinitions(
                new Nonterminal.Definition(plusNonterminal, TokenLibrary.intToken.asParseVariable(), TokenLibrary.plusToken.asParseVariable(), TokenLibrary.intToken.asParseVariable()),
                new Nonterminal.Definition(plusNonterminal, plusNonterminal.asParseVariable(), TokenLibrary.plusToken.asParseVariable(), plusNonterminal.asParseVariable())
        );
        */
        this.S.addDefinitions(
                new Nonterminal.Definition(S, TokenLibrary.a.asParseVariable(),  B.asParseVariable(), D.asParseVariable(), TokenLibrary.h.asParseVariable())
        );
        this.B.addDefinitions(
                new Nonterminal.Definition(B, TokenLibrary.c.asParseVariable(), C.asParseVariable())
        );

        this.C.addDefinitions(
                new Nonterminal.Definition(C, TokenLibrary.b.asParseVariable(), C.asParseVariable()),
                new Nonterminal.Definition(C)
        );

        this.D.addDefinitions(
                new Nonterminal.Definition(D, E.asParseVariable(), F.asParseVariable()),
                new Nonterminal.Definition(D)
        );

        this.E.addDefinitions(
                new Nonterminal.Definition(E, TokenLibrary.g.asParseVariable()),
                new Nonterminal.Definition(E)
        );
        this.F.addDefinitions(
                new Nonterminal.Definition(F, TokenLibrary.f.asParseVariable()),
                new Nonterminal.Definition(F)
        );

    }

    public Nonterminal getStartRule() {
        return S;
    }


    public ArrayList<Nonterminal> getNonterminals() {
        ArrayList<Nonterminal> nonterminals = new ArrayList<Nonterminal>();
        nonterminals.add(S);
        nonterminals.add(B);
        nonterminals.add(C);
        nonterminals.add(D);
        nonterminals.add(E);
        nonterminals.add(F);

        return nonterminals;
    }
}
