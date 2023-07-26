package Interpreter;

import ErrorManager.ErrorManager;
import InputBuffer.InputBuffer;
import LLParser.LLParser;
import Lexer.SymbolString;
import Lexer.TokenLibrary;
import Lexer.Tokenizer;
import Parser.Parser;
import Parser.ParseRuleLibrary;
import Parser.ParseTreeNode;
import LLParser.NonterminalLibrary;
import LLParser.LLParseTreeNode;

public class InterpretationSession {
    private final ErrorManager errorManager;
    private final InputBuffer inputBuffer;
    private final Tokenizer tokenizer;
    private final Parser parser;

    private final LLParser llParser;

    public InterpretationSession(String body) {
        this.errorManager = new ErrorManager();
        this.inputBuffer = new InputBuffer(body, errorManager);
        this.tokenizer = new Tokenizer(inputBuffer, errorManager);
        ParseRuleLibrary parseRuleLibrary = new ParseRuleLibrary();
        this.parser = new Parser(parseRuleLibrary.getStartRule(), parseRuleLibrary.getNonterminals(), tokenizer, errorManager);
        this.llParser = new LLParser(tokenizer, errorManager);
    }


    public void runSession() {


        SymbolString symbolString = tokenizer.extractAllSymbols();
        System.out.println(symbolString);
        llParser.setSymbols( symbolString.toList());

        // System.out.println("symbol string: "+symbolString);

        //ParseTreeNode ptn = parser.buildParseTree();

        //parser.makeFirstSets();
        //parser.makeFollowSets();

        //parser.printFirstSets();
        //parser.printFollowSets();

        //System.out.println(ptn.extractRepresentativeString());
        // ptn.printTreeRepresentation();


        NonterminalLibrary.file.apply(llParser);

        LLParseTreeNode parseTree = llParser.buildTree();

        parseTree.removeSymbolsOfType(TokenLibrary.whitespace);

        System.out.println(parseTree.getHierarchyString());




    }

}