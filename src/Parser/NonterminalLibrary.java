package Parser;

import ErrorManager.Error;
import Lexer.TokenLibrary;

public class NonterminalLibrary {
    public static Nonterminal file = new Nonterminal("file") {
        @Override
        public void apply(Parser parser) {
            MarkOpened opener = parser.open();

            while (!parser.eof()) {
                parser.eat(TokenLibrary.whitespace);

                //if (parser.at(TokenLibrary.let)) {
                //    letStatement.apply(parser);
                //} else {
                //    parser.advanceWithError(new Error(Error.ErrorType.PARSER_ERROR, "Expected a statement", false));
                //}
                expression.apply(parser);
            }

            parser.close(opener, TreeKind.valid(this));
        }
    };

    static Nonterminal letStatement = new Nonterminal("let") {
        @Override
        public void apply(Parser parser) {
            MarkOpened opener = parser.open();

            parser.expect(TokenLibrary.let);
            parser.eat(TokenLibrary.whitespace);
            parser.expect(TokenLibrary.identifier);
            parser.eat(TokenLibrary.whitespace);
            parser.expect(TokenLibrary.equals);
            parser.eat(TokenLibrary.whitespace);
            expression.apply(parser);

            parser.close(opener, TreeKind.valid(this));
        }
    };
    public static Nonterminal returnStatement = new Nonterminal("return") {
        @Override
        public void apply(Parser parser) {
            MarkOpened opener = parser.open();

            parser.expect(TokenLibrary.returnToken);
            parser.eat(TokenLibrary.whitespace);
            expression.apply(parser);
            parser.eat(TokenLibrary.whitespace);

            parser.close(opener, TreeKind.valid(this));
        }
    };

    public static Nonterminal lambda = new Nonterminal("lambda") {
        @Override
        public void apply(Parser parser) {
            MarkOpened opener = parser.open();

            parser.expect(TokenLibrary.lBrace);

            parameterList.apply(parser);

            parser.eat(TokenLibrary.whitespace);

            parser.expect(TokenLibrary.arrow);

            parser.eat(TokenLibrary.whitespace);

            do {
                expression.apply(parser);
                parser.eat(TokenLibrary.whitespace);
            } while (!parser.at(TokenLibrary.rBrace));

            parser.expect(TokenLibrary.rBrace);
            // Expression lambdas and statement lambdas are identical in the parse: expression lambdas contain 1 expression and statement lambdas contain multiple.

            parser.close(opener, TreeKind.valid(this));
        }
    };

    public static Nonterminal parameterList = new Nonterminal("parameter list") {
        @Override
        public void apply(Parser parser) {
            MarkOpened opener = parser.open();

            do {
                parser.eat(TokenLibrary.whitespace);
                expression.apply(parser);
                parser.eat(TokenLibrary.whitespace);
                parser.expect(TokenLibrary.identifier);
            } while (parser.eat(TokenLibrary.comma));

            parser.close(opener, TreeKind.valid(this));
        }
    };

    public static Nonterminal argumentList = new Nonterminal("argument list") {
        @Override
        public void apply(Parser parser) {
            MarkOpened opener = parser.open();

            do {
                parser.eat(TokenLibrary.whitespace);
                expression.apply(parser);
            } while (parser.eat(TokenLibrary.comma));

            parser.close(opener, TreeKind.valid(this));
        }
    };
    public static Nonterminal expression = new Nonterminal("expression") {
        @Override
        public void apply(Parser parser) {
            MarkOpened opener = parser.open();

            if (parser.at(TokenLibrary.lParen)) {
                parser.eat(TokenLibrary.lParen);
                expression.apply(parser);
                parser.expect(TokenLibrary.rParen);
            } else if (parser.at(TokenLibrary.intToken)) {
                parser.eat(TokenLibrary.intToken);
            } else if (parser.at(TokenLibrary.stringLiteral)) {
                parser.eat(TokenLibrary.stringLiteral);
            } else if (parser.at(TokenLibrary.identifier)) {
                parser.eat(TokenLibrary.identifier);

                if (parser.at(TokenLibrary.lParen)) {
                    parser.eat(TokenLibrary.lParen);

                    argumentList.apply(parser);

                    parser.expect(TokenLibrary.rParen);
                }
            } else if (parser.at(TokenLibrary.lBrace)) {
                lambda.apply(parser);
            } else if (parser.at(TokenLibrary.let)) {
                letStatement.apply(parser);
            } else if (parser.at(TokenLibrary.returnToken)) {
                returnStatement.apply(parser);
            }



            parser.close(opener, TreeKind.valid(this));
        }
    };
}
