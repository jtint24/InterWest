package Lexer;

public class Symbol {
    private String lexeme;
    private Token tokenType;

    public Symbol(String lexeme, Token tokenType) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
    }

    public String toString() {
        return "{ lexeme = `"+lexeme+"` tokenType = "+tokenType+" }";
    }
}
