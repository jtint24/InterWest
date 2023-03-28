package Parser;

import ErrorManager.ErrorManager;
import Lexer.SymbolString;
import Lexer.Token;
import java.util.Objects;

public class ParseVariable {
    private final Token token;
    private final Nonterminal nonterminal;


    public ParseVariable(Token t) {
        this.token = t;
        this.nonterminal = null;
    }

    public ParseVariable(Nonterminal nt) {
        this.token = null;
        this.nonterminal = nt;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        ParseVariable that = (ParseVariable) o;
        return Objects.equals(token, that.token) && Objects.equals(nonterminal, that.nonterminal);
    }

    @Override
    public int hashCode() {
        return Objects.hash(token, nonterminal);
    }

    public boolean isNonterminal() {
        return nonterminal != null;
    }
    public boolean isToken() {
        return token != null;
    }

    public Token getToken() {
        return token;
    }

    public Nonterminal getNonterminal() {
        return nonterminal;
    }


    /**
     * matches
     *
     * Returns a parseTree representing the ParseVariable's application to a given string. Returns null if the
     * ParseVariable doesn't match the string
     *
     * */
    public ParseTreeNode matches(SymbolString str, ErrorManager errorManager) {
        if (isToken()) {
            if (str.length() == 1 & str.get(0).getTokenType() == getToken()) {
                return new SymbolParseTreeNode(str.get(0));
            } else {
                return null;
            }
        } else {
            return getNonterminal().buildParseTree(str, errorManager);
        }
    }
}
