package Parser;

import Lexer.Symbol;

import java.util.Objects;

public class ParseVariable {
    private Symbol symbol;
    private Nonterminal nonterminal;


    public ParseVariable(Symbol s) {
        this.symbol = s;
        this.nonterminal = null;
    }

    public ParseVariable(Nonterminal nt) {
        this.symbol = null;
        this.nonterminal = nt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParseVariable that = (ParseVariable) o;
        return Objects.equals(symbol, that.symbol) && Objects.equals(nonterminal, that.nonterminal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(symbol, nonterminal);
    }

    public boolean isNonterminal() {
        return nonterminal != null;
    }
    public boolean isSymbol() {
        return symbol != null;
    }

    public Symbol getSymbol() {
        return symbol;
    }

    public Nonterminal getNonterminal() {
        return nonterminal;
    }
}
