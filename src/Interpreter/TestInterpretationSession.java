package Interpreter;

import Elements.ExpressionFunction;
import ErrorManager.Error;
import IO.OutputBuffer;
import Lexer.SymbolString;
import Parser.NonterminalLibrary;
import Parser.NonterminalParseTreeNode;
import Parser.ParseTreeNode;
import Regularity.DFA;
import Regularity.DFAConverter;
import Utils.Result;


import java.util.Arrays;
import java.util.function.Function;

public class TestInterpretationSession extends InterpretationSession {

    public TestInterpretationSession(String body) {
        super(body);
        this.outputBuffer.silence();
        this.outputBuffer.terse = true;
    }

    public OutputBuffer testGetParseTree() {
        try {
            SymbolString symbolString = tokenizer.extractAllSymbols();
            llParser.setSymbols(symbolString);
            NonterminalLibrary.file.apply(llParser);
            ParseTreeNode parseTree = llParser.buildTree();
            outputBuffer.println(parseTree);
        } catch (RuntimeException exception) {
            outputBuffer.println(exception);
            outputBuffer.println(Arrays.toString(exception.getStackTrace()));
        }

        return outputBuffer;
    }

    public OutputBuffer testExecution() {
        try {
            SymbolString symbolString = tokenizer.extractAllSymbols();

            llParser.setSymbols(symbolString);
            NonterminalLibrary.file.apply(llParser);
            ParseTreeNode parseTree = llParser.buildTree();

            Expression expr = expressionBuilder.buildNonterminalExpression((NonterminalParseTreeNode) parseTree);
            StaticReductionContext endStaticContext = expr.initializeStaticValues(new StaticReductionContext());
            errorManager.logErrors(endStaticContext.errors);

            ValidationContext startContext = new ValidationContext();
            ValidationContext endContext = expr.validate(startContext);
            errorManager.logErrors(endContext.errors);

            ExpressionResult s = expr.evaluate(new State(errorManager));

            outputBuffer.println(s.resultingState);
            outputBuffer.println(s.resultingValue);
        } catch (Exception e) {
            outputBuffer.println(e);
            outputBuffer.println(Arrays.toString(e.getStackTrace()));
        }
        return outputBuffer;
    }

    public OutputBuffer testDFAConversion() {
        try {
            SymbolString symbolString = tokenizer.extractAllSymbols();

            llParser.setSymbols(symbolString);
            NonterminalLibrary.file.apply(llParser);
            ParseTreeNode parseTree = llParser.buildTree();

            Expression programExpr = expressionBuilder.buildNonterminalExpression((NonterminalParseTreeNode) parseTree);
            StaticReductionContext staticReductionContext = new StaticReductionContext();
            programExpr.initializeStaticValues(staticReductionContext);

            ValidationContext startContext = new ValidationContext();
            ValidationContext endContext = programExpr.validate(startContext);

            errorManager.logErrors(endContext.errors);

            while (programExpr instanceof ExpressionContainer) {
                programExpr = ((ExpressionContainer) programExpr).getContainedExpressions().get(0);
            }


            if (programExpr instanceof IdentityExpression && ((IdentityExpression) programExpr).wrappedValue instanceof ExpressionFunction) {

                Expression innerExpression = ((ExpressionFunction) ((IdentityExpression) programExpr).wrappedValue).getWrappedExpression();
                // TODO: Make this use the already-in-place ExpressionFunction regularity conversion process
                Result<DFA, Function<ParseTreeNode, Error>> convertedDFAResult = DFAConverter.dfaFrom(innerExpression, "x");
                if (convertedDFAResult.isOK()) {
                    outputBuffer.println(convertedDFAResult.getOkValue());
                } else {
                    outputBuffer.println(convertedDFAResult.getErrValue().apply(((ExpressionFunction)((IdentityExpression) programExpr).wrappedValue).getWrappedExpression().underlyingParseTree));
                }
            } else {
                throw new RuntimeException("Expected a lambda expression");
                // errorManager.logError(new Error(Error.ErrorType.INTERPRETER_ERROR, "Expected a lambda expression", false));
            }


            return outputBuffer;


        } catch (RuntimeException exception) {
            outputBuffer.println(exception);
            outputBuffer.println(Arrays.toString(exception.getStackTrace()));
            return outputBuffer;
        }
    }

    public OutputBuffer testGetInterpretation() {
        try {
            SymbolString symbolString = tokenizer.extractAllSymbols();

            llParser.setSymbols(symbolString);
            NonterminalLibrary.file.apply(llParser);
            ParseTreeNode parseTree = llParser.buildTree();

            Expression expr = expressionBuilder.buildNonterminalExpression((NonterminalParseTreeNode) parseTree);
            StaticReductionContext endStaticContext = expr.initializeStaticValues(new StaticReductionContext());
            errorManager.logErrors(endStaticContext.errors);

            ValidationContext startContext = new ValidationContext();
            ValidationContext endContext = expr.validate(startContext);
            errorManager.logErrors(endContext.errors);


            outputBuffer.println(expr);
        } catch (RuntimeException exception) {
            // outputBuffer.println(exception);
            // outputBuffer.println(Arrays.toString(exception.getStackTrace()));
        }

        return outputBuffer;
    }

    public OutputBuffer testBinaryExpressions() {
        SymbolString symbolString = tokenizer.extractAllSymbols();
        llParser.setSymbols(symbolString);
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
            // outputBuffer.println(exception);
            // outputBuffer.println(Arrays.toString(exception.getStackTrace()));
        }

        return outputBuffer;
    }

    public OutputBuffer getOutputBuffer() {
        return outputBuffer;
    }
}
