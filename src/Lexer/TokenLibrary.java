package Lexer;

import java.util.HashSet;

public class TokenLibrary {
    public static Token[] getTokens() {
        return new Token[]{
            whitespace,
                floatToken,
                intToken,
                plusToken,
                let,
                equals,
                identifier
        };
    }

    public static final Token whitespace = new Token(
            "whitespace",
            (String lexeme) -> {
                return allIn(lexeme, " \t");
            },
            (String lexeme) -> {
                return allIn(lexeme, " \t");
            }
    );

    public static final Token identifier = new Token(
            "identifier",
            (String lexeme) -> allIn(lexeme.toLowerCase(), "qwertyuiopasdfghjklzxcvbnm") && !inKeywords(lexeme),
            (String lexeme) -> allIn(lexeme.toLowerCase(), "qwertyuiopasdfghjklzxcvbnm")
    );

    public static final Token plusToken = new Token(
            "plus",
            (String lexeme) -> {
                return lexeme.equals("+");
            },
            (String lexeme) -> {
                return "+".contains(lexeme);
            }
    );

    private static final Token floatToken = new Token(
            "float",
            (String lexeme) -> {
                return allIn(lexeme, "1234567890.") && 1 == countOf(lexeme, '.');
            },
            (String lexeme) -> {
                return allIn(lexeme, "1234567890.") && 2 > countOf(lexeme, '.');
            }
    );

    public static final Token intToken = new Token(
            "int",
            (String lexeme) -> {
                return allIn(lexeme, "1234567890");
            },
            (String lexeme) -> {
                return allIn(lexeme, "1234567890");
            }
    );

    public static final Token epsilon = new Token(
            "epsilon",
            (String lexeme) -> {
                return lexeme.length() == 0;
            },
            (String lexeme) -> {
                return lexeme.length() == 0;
            }
    );

    public static final Token eof = new Token(
            "eof",
            (String lexeme) -> {
                return false;
            },
            (String lexeme) -> {
                return false;
            }
    );

    public static final Token let = fromString("let");
    public static final Token equals = fromString("=");



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

    private static boolean inKeywords(String lexeme) {
        HashSet<String> keywords = new HashSet<>() {{
                add("let");
                add("match");
                add("if");
        }};

        return keywords.contains(lexeme);

    }

    private static Token fromString(String target) {
        return new Token(
                target,
                target::equals,
                target::startsWith
        );
    }

    public static final Token a = new Token("a", (a)->false, (a)->false);
    public static final Token b = new Token("b", (a)->false, (a)->false);
    public static final Token c = new Token("c", (a)->false, (a)->false);

    public static final Token d = new Token("d", (a)->false, (a)->false);
    public static final Token f = new Token("f", (a)->false, (a)->false);

    public static final Token g = new Token("g", (a)->false, (a)->false);
    public static final Token h = new Token("h", (a)->false, (a)->false);



    public static Token getEpsilon() {
        return epsilon;
    }

    public static Token getEOF() {
        return eof;
    }
}
