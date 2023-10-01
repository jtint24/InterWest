package Interpreter;

import ErrorManager.ErrorManager;
import IO.InputBuffer;
import IO.OutputBuffer;
import Lexer.SymbolString;
import Lexer.Tokenizer;
import Parser.Parser;
import Parser.NonterminalLibrary;
import Parser.NonterminalParseTreeNode;
import Parser.ParseTreeNode;


import java.util.Arrays;

public class TestInterpretationSession extends InterpretationSession {

    public TestInterpretationSession(String body) {
        super(body);
        this.outputBuffer.silence();
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

    public OutputBuffer testGetInterpretation() {
        try {
            SymbolString symbolString = tokenizer.extractAllSymbols();
            llParser.setSymbols(symbolString.toList());
            NonterminalLibrary.file.apply(llParser);
            ParseTreeNode parseTree = llParser.buildTree();
            Expression expr = expressionBuilder.buildExpression((NonterminalParseTreeNode) parseTree);


            State newState = new State(errorManager);
            ExpressionResult result = expr.evaluate(newState);
            outputBuffer.println(result.resultingState);
        } catch (RuntimeException exception) {
            outputBuffer.println(exception);
            outputBuffer.println(Arrays.toString(exception.getStackTrace()));
        }

        return outputBuffer;
    }

    public OutputBuffer testBinaryExpressions() {
        SymbolString symbolString = tokenizer.extractAllSymbols();
        llParser.setSymbols(symbolString.toList());
        NonterminalLibrary.fullExpression.apply(llParser);
        ParseTreeNode parseTree = llParser.buildTree();
        outputBuffer.println(parseTree);
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


}
