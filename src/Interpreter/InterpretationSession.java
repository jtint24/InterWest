package Interpreter;

import ErrorManager.ErrorManager;
import IO.InputBuffer;
import IO.OutputBuffer;
import Parser.Parser;
import Lexer.SymbolString;
import Lexer.TokenLibrary;
import Lexer.Tokenizer;
import Parser.NonterminalLibrary;
import Parser.ParseTreeNode;

import java.util.Arrays;

public class InterpretationSession {
    private final ErrorManager errorManager;
    private final OutputBuffer outputBuffer;
    private final InputBuffer inputBuffer;
    private final Tokenizer tokenizer;
    private final Parser llParser;

    public InterpretationSession(String body) {
        this(body, false);
    }
    public InterpretationSession(String body, boolean test) {
        this.outputBuffer = new OutputBuffer(test);
        this.errorManager = new ErrorManager(outputBuffer);
        this.inputBuffer = new InputBuffer(body, errorManager);
        this.tokenizer = new Tokenizer(inputBuffer, errorManager);
        this.llParser = new Parser(tokenizer, errorManager);
    }

    public OutputBuffer testGetParseTree() {
        try {
            SymbolString symbolString = tokenizer.extractAllSymbols();
            llParser.setSymbols(symbolString.toList());
            NonterminalLibrary.file.apply(llParser);
            ParseTreeNode parseTree = llParser.buildTree();
            outputBuffer.println(parseTree);
        } catch (RuntimeException exception) {
            outputBuffer.println(exception);
            outputBuffer.println(Arrays.toString(exception.getStackTrace()));
        }

        return outputBuffer;
    }

    public OutputBuffer testGetLexerString() {
        try {
            SymbolString symbolString = tokenizer.extractAllSymbols();
            outputBuffer.println(symbolString);
        } catch (RuntimeException exception) {
            outputBuffer.println(exception);
            outputBuffer.println(Arrays.toString(exception.getStackTrace()));
        }

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

        ParseTreeNode parseTree = llParser.buildTree();

        parseTree.removeSymbolsOfType(TokenLibrary.whitespace);

        errorManager.logErrors(parseTree.getMalformedNodeErrors());

        System.out.println(parseTree.getHierarchyString());


    }

}