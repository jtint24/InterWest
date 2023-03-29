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
        ParseRuleLibrary parseRuleLibrary = new ParseRuleLibrary();
        parser = new Parser(parseRuleLibrary.getStartRule(), parseRuleLibrary.getNonterminals(), errorManager);
    }


    public void runSession() {


        SymbolString symbolString = tokenizer.extractAllSymbols();

        System.out.println("symbol string: "+symbolString);

        ParseTreeNode ptn = parser.buildParseTree(symbolString);

        parser.makeFirstSets();

        parser.printFirstSets();

        System.out.println(ptn.extractRepresentativeString());
        ptn.printTreeRepresentation();
    }

}
