package LLParser;

import ErrorManager.Error;
import Lexer.TokenLibrary;

public class NonterminalLibrary {
    static Nonterminal letStatement = new Nonterminal("let") {
        @Override
        public void apply(LLParser parser) {
            MarkOpened opener = parser.open();

            parser.expect(TokenLibrary.let);
            parser.eat(TokenLibrary.whitespace);
            parser.expect(TokenLibrary.identifier);
            parser.eat(TokenLibrary.whitespace);
            parser.expect(TokenLibrary.equals);
            parser.eat(TokenLibrary.whitespace);
            parser.expect(TokenLibrary.intToken);

            parser.close(opener, TreeKind.valid(this));
        }
    };
    public static Nonterminal file = new Nonterminal("file") {
        @Override
        public void apply(LLParser parser) {
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
}
