package Lexer;

public class Symbol {
    private final String lexeme;
    private final Token tokenType;

    public Symbol(String lexeme, Token tokenType) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
    }

    public String toString() {
        return "{ lexeme = `"+lexeme.replace("\n", "\\n")+"` tokenType = "+tokenType+" }";
    }

    public Token getTokenType() {
        return tokenType;
    }

    public String getLexeme() {
        return lexeme;
    }
}
