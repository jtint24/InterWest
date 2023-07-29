package Interpreter;

import ErrorManager.ErrorManager;
import IO.InputBuffer;
import IO.OutputBuffer;
import LLParser.LLParser;
import Lexer.SymbolString;
import Lexer.TokenLibrary;
import Lexer.Tokenizer;
import LLParser.NonterminalLibrary;
import LLParser.LLParseTreeNode;

public class InterpretationSession {
    private final ErrorManager errorManager;
    private final OutputBuffer outputBuffer;
    private final InputBuffer inputBuffer;
    private final Tokenizer tokenizer;
    private final LLParser llParser;

    public InterpretationSession(String body) {
        this(body, false);
    }
    public InterpretationSession(String body, boolean test) {
        this.outputBuffer = new OutputBuffer(test);
        this.errorManager = new ErrorManager(outputBuffer);
        this.inputBuffer = new InputBuffer(body, errorManager);
        this.tokenizer = new Tokenizer(inputBuffer, errorManager);
        this.llParser = new LLParser(tokenizer, errorManager);
    }

    public OutputBuffer testGetParseTree() {
        try {
            SymbolString symbolString = tokenizer.extractAllSymbols();
            llParser.setSymbols(symbolString.toList());
            NonterminalLibrary.file.apply(llParser);
            LLParseTreeNode parseTree = llParser.buildTree();
            outputBuffer.println(parseTree);
        } catch (RuntimeException ignored) {}

        return outputBuffer;
    }

    public OutputBuffer testGetLexerString() {
        try {
            SymbolString symbolString = tokenizer.extractAllSymbols();
            outputBuffer.println(symbolString);
        } catch (RuntimeException ignored) {}

        return outputBuffer;
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

        errorManager.logErrors(parseTree.getMalformedNodeErrors());

        System.out.println(parseTree.getHierarchyString());


    }

}