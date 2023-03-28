package Interpreter;

import ErrorManager.ErrorManager;
import InputBuffer.InputBuffer;
import Lexer.SymbolString;
import Lexer.Tokenizer;
import Parser.Parser;
import Parser.ParseRuleLibrary;
import Parser.ParseTreeNode;

public class InterpretationSession {
    private final ErrorManager errorManager;
    private final InputBuffer inputBuffer;
    private final Tokenizer tokenizer;
    private final Parser parser;

    public InterpretationSession(String body) {
        errorManager = new ErrorManager();
        inputBuffer = new InputBuffer(body, errorManager);
        tokenizer = new Tokenizer(inputBuffer, errorManager);
        parser = new Parser(ParseRuleLibrary.getStartRule(), errorManager);
    }


    public void runSession() {
        SymbolString symbolString = tokenizer.extractAllSymbols();
        ParseTreeNode ptn = parser.buildParseTree(symbolString);

        System.out.println(ptn.extractRepresentativeString());
    }

}
