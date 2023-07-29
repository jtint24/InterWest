package Parser;

import ErrorManager.Error;
import Lexer.TokenLibrary;

public class NonterminalLibrary {
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
    public static Nonterminal file = new Nonterminal("file") {
        @Override
        public void apply(Parser parser) {
            MarkOpened opener = parser.open();

            while (!parser.eof()) {
                parser.eat(TokenLibrary.whitespace);
                if (parser.at(TokenLibrary.let)) {
                    letStatement.apply(parser);
                } else {
                    parser.advanceWithError(new Error(Error.ErrorType.PARSER_ERROR, "Expected a statement", false));
                }
            }

            parser.close(opener, TreeKind.valid(this));
        }
    };

    public static Nonterminal argumentList = new Nonterminal("Argument List") {
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
    public static Nonterminal expression = new Nonterminal("Expression") {
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
            }



            parser.close(opener, TreeKind.valid(this));
        }
    };
}
