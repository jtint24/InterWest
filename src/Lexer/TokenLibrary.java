package Lexer;

public class TokenLibrary {
    public static Token[] getTokens() {
        return new Token[]{
            whitespace,
                floatToken,
                intToken,
                plus
        };
    }

    private static final Token whitespace = new Token(
            "whitespace",
            (String lexeme) -> {
                return allIn(lexeme, " \t");
            },
            (String lexeme) -> {
                return allIn(lexeme, " \t");
            }
    );

    public static final Token plus = new Token(
            "plus",
            (String lexeme) -> {
                return lexeme.equals("+");
            },
            (String lexeme) -> {
                return "+".contains(lexeme);
            }
    );

    private static final Token floatToken = new Token(
            "floatToken",
            (String lexeme) -> {
                return allIn(lexeme, "1234567890.") && 1 == countOf(lexeme, '.');
            },
            (String lexeme) -> {
                return allIn(lexeme, "1234567890.") && 2 > countOf(lexeme, '.');
            }
    );

    public static final Token intToken = new Token(
            "intToken",
            (String lexeme) -> {
                return allIn(lexeme, "1234567890");
            },
            (String lexeme) -> {
                return allIn(lexeme, "1234567890");
            }
    );


    private static boolean allIn(String lexeme, String validChars) {
        for (char lexemeChar : lexeme.toCharArray()) {
            if (!validChars.contains("" + lexemeChar)) {
                return false;
            }
        }

        return true;
    }

    private static int countOf(String lexeme, char c) {
        int count = 0;
        for (char lexemeChar : lexeme.toCharArray()) {
            if (lexemeChar == c) {
                count ++;
            }
        }
        return count;
    }

}
