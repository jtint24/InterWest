package ErrorManager;

import Elements.Type;
import Interpreter.*;
import Lexer.Token;
import Parser.NonterminalParseTreeNode;
import Parser.ParseTreeNode;
import Parser.TreeKind;

import java.util.List;

public class ErrorLibrary {
    // TODO: Add information about the whole line
    public static Error getIfTypeMismatch(ConditionalExpression conditionalExpression, Type actual) {
        Annotator annotator = new Annotator(conditionalExpression.underlyingParseTree);
        annotator.applyStyle(
                conditionalExpression.getCondition().underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This has type `"+actual+"`")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nThe condition of this if statement has type `"+actual+"`, which doesn't match what I expected, type `Bool`.\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "type mismatch",
                bodyMessage,
                true,
                0);
    }

    public static Error getIfTypeWarning(ConditionalExpression conditionalExpression, Type actual) {
        Annotator annotator = new Annotator(conditionalExpression.underlyingParseTree);
        annotator.applyStyle(
                conditionalExpression.getCondition().underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This has type `"+actual+"`")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nThe condition of this if statement has type `"+actual+"`, and I expected type `Bool`.\nThese types could still match! I just can't prove that they'll match all the time. This CAN cause a runtime error!\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "type warning",
                bodyMessage,
                false,
                0,
                "Check the definition of "+actual+"! If it's not regular, making it regular will help me prove its equivalence.",
                "If you simplify the condition to something that I can detect the value of statically, I'll be able to prove its equivalence."
                );
    }

    public static Error getCallabilityMismatch(FunctionExpression functionExpression, Type actual) {
        Annotator annotator = new Annotator(functionExpression.underlyingParseTree);
        annotator.applyStyle(
                functionExpression.getFuncExpression().underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This isn't a function.")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nI was expecting a function here, but instead this has type `"+actual+"`.\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "callability mismatch",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getFunctionArgumentTypeMismatch(FunctionExpression functionExpression, Expression invalidArg, Type expectedType, Type actualType) {
        Annotator annotator = new Annotator(functionExpression.underlyingParseTree);
        annotator.applyStyle(
                invalidArg.underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This has type `"+actualType+"`")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nI was expecting an expression of type `"+expectedType+"` in this argument, but I got type `"+actualType+"`.\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "type mismatch",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getFunctionArgumentTypeWarning(FunctionExpression functionExpression, Expression invalidArg, Type expectedType, Type actualType) {
        Annotator annotator = new Annotator(functionExpression.underlyingParseTree);
        annotator.applyStyle(
                invalidArg.underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This has type `"+actualType+"`")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nThe condition of this if statement has type `"+actualType+"`, and I expected type `"+expectedType+"`.\nThese types could still match! I just can't prove that they'll match all the time. This CAN cause a runtime error!\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "type mismatch",
                bodyMessage,
                false,
                0
        );
    }

    public static Error getVariableNotFound(VariableExpression variableExpression) {
        Annotator annotator = new Annotator(variableExpression.underlyingParseTree);
        annotator.applyStyle(
                variableExpression.underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "I don't recognize this")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nI can't find a constant called `"+variableExpression+"` in scope.\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "unknown constant",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getRedefinition(LetExpression letExpression, String name) {
        Annotator annotator = new Annotator(letExpression.underlyingParseTree);
        ParseTreeNode redeclarationPTNode = ((NonterminalParseTreeNode)letExpression.underlyingParseTree).getChildren().get(1);
        annotator.applyStyle(
                redeclarationPTNode,
                new Annotator.Style(AnsiCodes.RED, '^', "This constant exists in scope")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nThis constant, `"+name+"`, already exists and can't be redeclared.\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "redeclared constant",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getNotReturnable(ReturnExpression returnExpression) {
        Annotator annotator = new Annotator(returnExpression.underlyingParseTree);
        annotator.applyStyle(
                returnExpression.underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This statement can't return")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nI don't actually see any scope for this statement to return from.\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "not returnable",
                bodyMessage,
                true,
                0,
                "Make sure that this return statement is in a function"
        );
    }

    public static Error getReturnTypeMismatch(ReturnExpression returnExpression, Type expectedType, Type actualType) {
        Annotator annotator = new Annotator(returnExpression.underlyingParseTree);
        annotator.applyStyle(
                returnExpression.getExprToReturn().underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This has type `"+actualType+"`")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nI was expecting an expression of type `"+expectedType+"` but here we're returning a value of `"+actualType+"`.\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "type mismatch",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getReturnTypeWarning(ReturnExpression returnExpression, Type expectedType, Type actualType) {
        Annotator annotator = new Annotator(returnExpression.underlyingParseTree);
        annotator.applyStyle(
                returnExpression.getExprToReturn().underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This has type `"+actualType+"`")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nHere you're returning a value of type `"+actualType+"`, and I expected type `"+expectedType+"`.\nThese types could still match! I just can't prove that they'll match all the time. This CAN cause a runtime error!\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "type mismatch",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getUnknownToken(String currentLexeme) {
        // TODO: Print the line, not just the lexeme. Use Annotator

        String bodyMessage = "I don't recognize the token `"+currentLexeme+"`";
        return new Error(
                Error.ErrorType.LEXER_ERROR,
                "unknown token",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getWrongToken(Token expected, Token actualToken) {

        String bodyMessage = "On this line, I was expecting a `"+expected+"` but received `"+actualToken+"`, which doesn't seem to match";

        return new Error(
                Error.ErrorType.PARSER_ERROR,
                "Unexpected Token",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getParserHasTerminated() {
        String bodyMessage = "I was expecting this code to continue, but it ended here!";
        return new Error(
                Error.ErrorType.PARSER_ERROR,
                "Unexpected Termination",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getSyntaxError(NonterminalParseTreeNode ptNode) {
        Annotator annotator = new Annotator(ptNode);
        annotator.applyStyle(
                ptNode,
                new Annotator.Style(AnsiCodes.RED, '^', "I don't recognize this")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nI don't recognize this pattern.\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "syntax error",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getRuntimeArgumentTypeMismatch(FunctionExpression functionExpression, Expression invalidArg, Type expectedType, Type actualType) {
        Annotator annotator = new Annotator(functionExpression.underlyingParseTree);
        annotator.applyStyle(
                invalidArg.underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This has type `"+actualType+"`")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nI was expecting an expression of type `"+expectedType+"` in this argument, but I got type `"+actualType+"`.\n";
        return new Error(
                Error.ErrorType.RUNTIME_ERROR,
                "type mismatch",
                bodyMessage,
                true,
                0
        );
    }



}
