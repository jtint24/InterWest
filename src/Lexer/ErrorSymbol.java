package Lexer;

import ErrorManager.ErrorLibrary;
import ErrorManager.Error;

public class ErrorSymbol extends Symbol {
    public ErrorSymbol(String lexeme, int startingLineNumber) {
        super(lexeme, TokenLibrary.errorToken, startingLineNumber);
    }

    public Error getError() {
        return ErrorLibrary.getUnknownToken(getLexeme());
    }
}
