package Lexer;

import java.util.HashSet;

public class TokenLibrary {
    public static Token[] getTokens() {
        return new Token[]{
            whitespace,
                floatToken,
                intToken,
                stringLiteral,
                identifier,
                let,
                returnToken,
                plusToken,
                equals,
                lParen,
                rParen,
                lBrace,
                rBrace,
                lBracket,
                rBracket,
                comma,
                arrow
        };
    }


    public static final Token whitespace = new Token(
            "whitespace",
            (String lexeme) -> {
                return allIn(lexeme, " \t\n");
            },
            (String lexeme) -> {
                return allIn(lexeme, " \t\n");
            }
    );

    public static final Token lParen = fromString("(");
    public static final Token rParen = fromString(")");

    public static final Token lBrace = fromString("{");
    public static final Token rBrace = fromString("}");
    public static final Token lBracket = fromString("[");
    public static final Token rBracket = fromString("]");
    public static final Token arrow = fromString("->");

    public static final Token comma = fromString(",");

    public static final Token stringLiteral = new Token(
            "String",
            (String lexeme) -> {
                if (!lexeme.startsWith("\"") || !lexeme.endsWith("\"") || lexeme.length() < 2) {
                    return false;
                }
                if (lexeme.equals("\"\"")) {
                    return true;
                }
                return !lexeme.substring(1, lexeme.length() - 1).contains("\"");
            },
            (String lexeme) -> {
                if (!lexeme.startsWith("\"")) {
                    return false;
                }
                if (lexeme.length() > 2 && lexeme.substring(1, lexeme.length()-1).contains("\"")) {
                    return false;
                }
                return true;
            }
    );


    public static final Token identifier = new Token(
            "identifier",
            (String lexeme) -> {
                    if (inKeywords(lexeme)) {
                        return false;
                    }
                    if (lexeme.length() == 0) {
                        return true;
                    }
                    return  (Character.isLetter(lexeme.charAt(0)) || lexeme.charAt(0) == '_') && (allIn(lexeme.toLowerCase(), "1234567890qwertyuiopasdfghjklzxcvbnm_"));
                },
            (String lexeme) -> {
                if (lexeme.length() == 0) {
                    return true;
                }
                return  (Character.isLetter(lexeme.charAt(0)) || lexeme.charAt(0) == '_') && (allIn(lexeme.toLowerCase(), "1234567890qwertyuiopasdfghjklzxcvbnm_"));
            }
    );

    public static final Token plusToken = new Token(
            "plus",
            (String lexeme) -> {
                return lexeme.equals("+");
            },
            (String lexeme) -> {
                return "+".contains(lexeme);
            }
    ).toBinder(new BindingPowers(PrecedenceLevel.ADDITION, Associativity.LEFT));

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
                if (lexeme.length() == 0) {
                    return false;
                }
                return (lexeme.charAt(0) == '-' || Character.isDigit(lexeme.charAt(0))) && allIn(lexeme.substring(1), "1234567890");
            },
            (String lexeme) -> {
                if (lexeme.length() == 0) {
                    return false;
                }
                return (lexeme.charAt(0) == '-' || Character.isDigit(lexeme.charAt(0))) && allIn(lexeme.substring(1), "1234567890");
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
    public static final Token returnToken = fromString("return");
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
                add("else");
                add("return");
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
