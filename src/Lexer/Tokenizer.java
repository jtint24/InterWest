package Lexer;

import ErrorManager.ErrorManager;
import ErrorManager.Error;
import IO.InputBuffer;

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

        ArrayList<Token> possibleTokens;
        String originalLexeme = currentLexeme;

        do {
            possibleTokens = new ArrayList<>();

            for (Token possibleToken : TokenLibrary.getTokens()) {
                if (possibleToken.isCurrentlyValid(currentLexeme)) {
                    possibleTokens.add(possibleToken);
                }
            }

            if (possibleTokens.size() == 1) {
                return new Symbol(currentLexeme, possibleTokens.get(0));
            } else if (possibleTokens.size() > 1) {
                errorManager.logError(new Error(Error.ErrorType.LEXER_ERROR, "Ambiguous tokens for `"+currentLexeme+"`", true));
            }

            inputBuffer.ungetChar(currentLexeme.charAt(currentLexeme.length() - 1));
            currentLexeme = currentLexeme.substring(0, currentLexeme.length() - 1);
        } while (!currentLexeme.equals(""));

        errorManager.logError(new Error(Error.ErrorType.LEXER_ERROR, "No possible token for `"+originalLexeme+"`", true));

        // System.out.println("currentLexeme: `"+currentLexeme+"`");


        return null;
    }

    public boolean isTerminated() {
        return inputBuffer.isTerminated();
    }
}
