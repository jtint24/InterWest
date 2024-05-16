package Lexer;

public class Symbol {
    private final String lexeme;
    private final Token tokenType;
    private final int startingLineNumber; // For tokens that span multiple lines, ie, whitespace with newlines, this is only the starting line number

    public Symbol(String lexeme, Token tokenType, int startingLineNumber) {
        this.lexeme = lexeme;
        this.tokenType = tokenType;
        this.startingLineNumber = startingLineNumber;
    }

    public String toString() {
        return "{ lexeme = `"+lexeme.replace("\n", "\\n")+"` tokenType = "+tokenType+" lineNumber = "+startingLineNumber+" }";
    }

    public Token getTokenType() {
        return tokenType;
    }

    public String getLexeme() {
        return lexeme;
    }
    public int getStartingLineNumber() {
        return startingLineNumber;
    }
}
