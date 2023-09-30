package Parser;

import Lexer.BinderToken;
import Lexer.Token;
import Lexer.TokenLibrary;

public class NonterminalLibrary {
    public static Nonterminal file = new Nonterminal("file") {
        @Override
        public void parse(Parser parser) {

            while (!parser.eof()) {
                parser.eat(TokenLibrary.whitespace);

                //if (parser.at(TokenLibrary.let)) {
                //    letStatement.apply(parser);
                //} else {
                //    parser.advanceWithError(new Error(Error.ErrorType.PARSER_ERROR, "Expected a statement", false));
                //}
                fullExpression.apply(parser);
            }

        }
    };

    static Nonterminal letStatement = new Nonterminal("let") {
        @Override
        public void parse(Parser parser) {

            parser.expect(TokenLibrary.let);
            parser.eat(TokenLibrary.whitespace);
            parser.expect(TokenLibrary.identifier);
            parser.eat(TokenLibrary.whitespace);
            parser.expect(TokenLibrary.equals);
            parser.eat(TokenLibrary.whitespace);
            fullExpression.apply(parser);

        }
    };
    public static Nonterminal returnStatement = new Nonterminal("return") {
        @Override
        public void parse(Parser parser) {

            parser.expect(TokenLibrary.returnToken);
            parser.eat(TokenLibrary.whitespace);
            fullExpression.apply(parser);
            parser.eat(TokenLibrary.whitespace);

        }
    };

    public static Nonterminal lambda = new Nonterminal("lambda") {
        @Override
        public void parse(Parser parser) {

            parser.expect(TokenLibrary.lBrace);

            parameterList.apply(parser);

            parser.eat(TokenLibrary.whitespace);

            parser.expect(TokenLibrary.arrow);

            parser.eat(TokenLibrary.whitespace);

            do {
                fullExpression.apply(parser);
                parser.eat(TokenLibrary.whitespace);
            } while (!parser.at(TokenLibrary.rBrace));

            parser.expect(TokenLibrary.rBrace);
            // Expression lambdas and statement lambdas are identical in the parse: expression lambdas contain 1 expression and statement lambdas contain multiple.

        }
    };

    public static Nonterminal parameterList = new Nonterminal("parameter list") {
        @Override
        public void parse (Parser parser) {

            do {
                parser.eat(TokenLibrary.whitespace);
                fullExpression.apply(parser);
                parser.eat(TokenLibrary.whitespace);
                parser.expect(TokenLibrary.identifier);
            } while (parser.eat(TokenLibrary.comma));

        }
    };

    public static Nonterminal argumentList = new Nonterminal("argument list") {
        @Override
        public void parse(Parser parser) {

            do {
                parser.eat(TokenLibrary.whitespace);
                fullExpression.apply(parser);
            } while (parser.eat(TokenLibrary.comma));

        }
    };

    // Basic expressions: including expressions in parentheses, literals, identifiers, lambdas
    public static Nonterminal delimitedExpression = new Nonterminal("delimitedExpression") {
        @Override
        public void parse(Parser parser) {
            if (parser.at(TokenLibrary.lParen)) {
                // parser.eat(TokenLibrary.lParen);
                funcCallExpression.apply(parser);
                // parser.expect(TokenLibrary.rParen);
            } else if (parser.at(TokenLibrary.intToken)) {
                parser.eat(TokenLibrary.intToken);
            } else if (parser.at(TokenLibrary.stringLiteral)) {
                parser.eat(TokenLibrary.stringLiteral);
            } else if (parser.at(TokenLibrary.identifier)) {
                parser.eat(TokenLibrary.identifier);
            } else if (parser.at(TokenLibrary.lBrace)) {
                lambda.apply(parser);
            } else if (parser.at(TokenLibrary.let)) {
                letStatement.apply(parser);
            } else if (parser.at(TokenLibrary.returnToken)) {
                returnStatement.apply(parser);
            }
            parser.eat(TokenLibrary.whitespace);
        }
    };


    // Argument list in parentheses constituting a function call
    public static Nonterminal expressionCall = new Nonterminal("expression call") {
        @Override
        public void parse(Parser parser) {
            parser.expect(TokenLibrary.lParen);

            argumentList.apply(parser);

            parser.expect(TokenLibrary.rParen);
            parser.eat(TokenLibrary.whitespace);
        }
    };

    // Complex expressions, including function calls
    public static Nonterminal funcCallExpression = new Nonterminal("func call expression") {
        @Override
        public void parse(Parser parser) {
            MarkClosed leftSide = expressionCall.apply(parser);

            if (parser.at(TokenLibrary.lParen)) {
                // Parses function calls
                while (parser.at(TokenLibrary.lParen)) {
                    expressionCall.apply(parser, leftSide);
                }
            }
        }
    };

    public static Nonterminal fullExpression = new Nonterminal("full expression") {
        @Override
        public void parse(Parser parser) {
            recursiveExpression(parser, TokenLibrary.eof);
        }

        public void recursiveExpression(Parser parser, Token leftToken) {
            MarkClosed lefthandSide = delimitedExpression.apply(parser);
            // System.out.println("Recursively parsing with left "+leftToken);

            // while (parser.at(TokenLibrary.lParen)) {
            //     MarkOpened opener = parser.openBefore(lefthandSide);
            // }



            while (!parser.eof()) {
                Token rightToken = parser.nth(0);
                // System.out.println("Right token is "+rightToken);

                // if (rightToken instanceof BinderToken) {
                    // System.out.println("rbp: "+((BinderToken) rightToken).leftBindingPower);
                // }
                // if (leftToken instanceof BinderToken) {
                    // System.out.println("lbp: "+((BinderToken) leftToken).rightBindingPower);
                // }

                if (Token.rightBindsTighter(leftToken, rightToken)) {
                    // System.out.println("Right binds tighter.");

                    MarkOpened opener = parser.openBefore(lefthandSide);
                    parser.advance();
                    recursiveExpression(parser, rightToken);
                    lefthandSide = parser.close(opener, TreeKind.valid(binaryExpression));
                } else {
                    // System.out.println("Left binds tighter: BREAK!");

                    break;
                }
            }
        }
    };

    public static Nonterminal binaryExpression = new Nonterminal("binary expression") {
        @Override
        public void parse(Parser parser) {}
    };

}
