package Lexer;

import ErrorManager.ErrorManager;
import ErrorManager.Error;
import IO.InputBuffer;
import Utils.Result;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static ErrorManager.ErrorLibrary.getUnknownToken;

public class Tokenizer {
    private final InputBuffer inputBuffer;
    private final ErrorManager errorManager;
    private int currentLineNumber = 0;

    public Tokenizer(InputBuffer inputBuffer, ErrorManager errorManager) {
        this.inputBuffer = inputBuffer;
        this.errorManager = errorManager;
    }

    public SymbolString extractAllSymbols() {
        ArrayList<Symbol> retList = new ArrayList<>();
        ArrayList<Error> errors = new ArrayList<>();
        while (!inputBuffer.isTerminated()) {
            Result<Symbol, ErrorSymbol> symbol = getSymbol();
            if (!symbol.isOK()) {
                errors.add(symbol.getErrValue().getError());
                retList.add(symbol.getErrValue());
            } else {
                retList.add(symbol.getOkValue());
            }
        }

        errorManager.logErrors(errors);

        return new SymbolString(retList);
    }

    public Result<Symbol, ErrorSymbol> getSymbol() {
        List<Token> availableTokens = Arrays.asList(TokenLibrary.getTokens());
        String currentLexeme = "";

        int lineNumberDifference = 0;

        while (!availableTokens.isEmpty()) {
            if (inputBuffer.isTerminated()) {
                break;
            } else {
                char currentChar = inputBuffer.getChar();
                currentLexeme += currentChar;
                if (currentChar == '\n') {
                    lineNumberDifference++;
                }
            }

            List<Token> newAvailableTokens = new ArrayList<>();

            for (Token availableToken : availableTokens) {
                if (availableToken.couldBeValid(currentLexeme)) {
                    newAvailableTokens.add(availableToken);
                }
            }

            availableTokens = newAvailableTokens;
        }

        ArrayList<Token> possibleTokens;
        String originalLexeme = currentLexeme;

        // Now that we have an initial lexeme, we need to make sure that it actually matches the possible token type
        // We do this by checking if the lexeme matches any tokens' isCurrentlyValid functions, shortening the lexeme
        // until it matches exactly one token. If it matches more than one, then the set of token types is ambiguous

        do {
            possibleTokens = new ArrayList<>();

            for (Token possibleToken : TokenLibrary.getTokens()) {
                if (possibleToken.isCurrentlyValid(currentLexeme)) {
                    possibleTokens.add(possibleToken);
                }
            }

            if (possibleTokens.size() == 1) {
                currentLineNumber += lineNumberDifference;
                return Result.ok(new Symbol(currentLexeme, possibleTokens.get(0), currentLineNumber - lineNumberDifference));
            } else if (possibleTokens.size() > 1) {
               // errorManager.logError(new Error(Error.ErrorType.LEXER_ERROR, "Ambiguous tokens for `"+currentLexeme+"`", true));
                throw new RuntimeException("Ambiguous tokens for `"+currentLexeme+"`:"+Arrays.toString(possibleTokens.toArray()));
            }

            inputBuffer.ungetChar(currentLexeme.charAt(currentLexeme.length() - 1));
            currentLexeme = currentLexeme.substring(0, currentLexeme.length() - 1);
        } while (currentLexeme.length() > 0);

        // errorManager.logError(getUnknownToken(originalLexeme));
        currentLineNumber += lineNumberDifference;

        // Remove characters from the input buffer to remove the error lexeme
        for (int i = 0; i<originalLexeme.length(); i++) {
            inputBuffer.getChar();
        }


        return Result.error(new ErrorSymbol(originalLexeme, currentLineNumber - lineNumberDifference));
    }

    public boolean isTerminated() {
        return inputBuffer.isTerminated();
    }
}
