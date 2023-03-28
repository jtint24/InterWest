package Lexer;

import ErrorManager.ErrorManager;
import ErrorManager.Error;
import InputBuffer.InputBuffer;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Tokenizer {
    private final InputBuffer inputBuffer;
    private final ErrorManager errorManager;

    public Tokenizer(InputBuffer inputBuffer, ErrorManager errorManager) {
        this.inputBuffer = inputBuffer;
        this.errorManager = errorManager;
    }

    public SymbolString extractAllSymbols() {
        ArrayList<Symbol> retList = new ArrayList<>();
        while (!inputBuffer.isTerminated()) {
            retList.add(getSymbol());
        }
        return new SymbolString(retList);
    }

    public Symbol getSymbol() {
        List<Token> availableTokens = Arrays.asList(TokenLibrary.getTokens());
        String currentLexeme = "";

        while (!availableTokens.isEmpty()) {
            if (inputBuffer.isTerminated()) {
                break;
            } else {
                currentLexeme += inputBuffer.getChar();
            }

            List<Token> newAvailableTokens = new ArrayList<>();

            for (Token availableToken : availableTokens) {
                if (availableToken.couldBeValid(currentLexeme)) {
                    newAvailableTokens.add(availableToken);
                }
            }

            availableTokens = newAvailableTokens;
        }

        if (!inputBuffer.isTerminated()) {
            inputBuffer.ungetChar(currentLexeme.charAt(currentLexeme.length() - 1));
            currentLexeme = currentLexeme.substring(0, currentLexeme.length() - 1);
        }

        // System.out.println("currentLexeme: `"+currentLexeme+"`");

        ArrayList<Token> possibleTokens = new ArrayList<>();

        for (Token possibleToken : TokenLibrary.getTokens()) {
            if (possibleToken.isCurrentlyValid(currentLexeme)) {
                possibleTokens.add(possibleToken);
            }
        }

        if (possibleTokens.size() == 1) {
            return new Symbol(currentLexeme, possibleTokens.get(0));
        } else if (possibleTokens.size() == 0) {
            errorManager.logError(new Error(Error.ErrorType.LEXER_ERROR, "No possible tokens for "+currentLexeme, true));
        } else {
            errorManager.logError(new Error(Error.ErrorType.LEXER_ERROR, "Ambiguous tokens for "+currentLexeme, true));
        }

        return null;
    }
}
