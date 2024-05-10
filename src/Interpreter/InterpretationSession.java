package Interpreter;

import ErrorManager.ErrorManager;
import IO.InputBuffer;
import IO.OutputBuffer;
import Parser.Parser;
import Lexer.SymbolString;
import Lexer.TokenLibrary;
import Lexer.Tokenizer;
import Parser.NonterminalLibrary;
import Parser.NonterminalParseTreeNode;
import Parser.ParseTreeNode;

import java.util.Arrays;

public class InterpretationSession {
    ErrorManager errorManager;
    OutputBuffer outputBuffer;
    InputBuffer inputBuffer;
    Tokenizer tokenizer;
    Parser llParser;
    ExpressionBuilder expressionBuilder;

    public InterpretationSession(String body) {
        this.outputBuffer = new OutputBuffer();
        this.errorManager = new ErrorManager(outputBuffer);
        this.inputBuffer = new InputBuffer(body, errorManager);
        this.tokenizer = new Tokenizer(inputBuffer, errorManager);
        this.llParser = new Parser(tokenizer, errorManager);
        this.expressionBuilder = new ExpressionBuilder(errorManager);
    }

    public void runSession() {

        Expression expr = getAST();

        outputBuffer.println(expr);
        expr.initializeStaticValues(new StaticReductionContext());
        ValidationContext validationContext = expr.validate(new ValidationContext());
        errorManager.logErrors(validationContext.errors);

        System.out.println("ValidationContext: ");
        System.out.println(validationContext.errors);

        State newState = new State(errorManager);
        ExpressionResult result = expr.evaluate(newState);

        outputBuffer.println(result.resultingValue);
        outputBuffer.println(result.resultingState);

    }

    public Expression getAST() {
        SymbolString symbolString = tokenizer.extractAllSymbols();
        outputBuffer.println(symbolString);
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

        outputBuffer.println(parseTree);

        Expression expr = expressionBuilder.buildExpression((NonterminalParseTreeNode) parseTree);
        return expr;
    }

}