package ErrorManager;

import Elements.Type;
import Elements.TypeExpression;
import Interpreter.*;
import Lexer.Symbol;
import Lexer.SymbolString;
import Lexer.Token;
import Lexer.TokenLibrary;
import Parser.NonterminalParseTreeNode;
import Parser.ParseTreeNode;
import Parser.TerminalParseTreeNode;
import Parser.TreeKind;

import java.util.ArrayList;
import java.util.List;

public class ErrorLibrary {
    // TODO: Add information about the whole line
    public static Error getIfTypeMismatch(ConditionalExpression conditionalExpression, Type actual) {
        Annotator annotator = new Annotator(conditionalExpression.underlyingParseTree.getLine());
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
        Annotator annotator = new Annotator(conditionalExpression.underlyingParseTree.getLine());
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
        Annotator annotator = new Annotator(functionExpression.underlyingParseTree.getLine());
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
        Annotator annotator = new Annotator(functionExpression.underlyingParseTree.getLine());
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
        Annotator annotator = new Annotator(functionExpression.underlyingParseTree.getLine());
        annotator.applyStyle(
                invalidArg.underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This has type `"+actualType+"`")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nThe argument of this function has type `"+actualType+"`, and I expected type `"+expectedType+"`.\nThese types could still match! I just can't prove that they'll match all the time. This CAN cause a runtime error!\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "type mismatch",
                bodyMessage,
                false,
                0
        );
    }

    public static Error getVariableNotFound(VariableExpression variableExpression) {
        Annotator annotator = new Annotator(variableExpression.underlyingParseTree.getLine());
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
        Annotator annotator = new Annotator(letExpression.underlyingParseTree.getLine());
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
        Annotator annotator = new Annotator(returnExpression.underlyingParseTree.getLine());
        annotator.applyStyle(
                ((NonterminalParseTreeNode)returnExpression.underlyingParseTree).getChildren().get(0),
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
        Annotator annotator = new Annotator(returnExpression.underlyingParseTree.getLine());
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
        Annotator annotator = new Annotator(returnExpression.underlyingParseTree.getLine());
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

    public static Error getWrongToken(SymbolString line, Token expected, Symbol actualSymbol) {

        Annotator annotator = new Annotator(line);
        annotator.applyStyle(new TerminalParseTreeNode(actualSymbol, new SymbolString(actualSymbol)), new Annotator.Style(AnsiCodes.RED, '^'));

        String bodyMessage = annotator.getAnnotatedString()+"\n\nOn this line, I was expecting a `"+expected+"` but received `"+actualSymbol.getLexeme()+"`, which doesn't seem to match";

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
        Annotator annotator = new Annotator(ptNode.getLine());
        annotator.applyStyle(
                ptNode,
                new Annotator.Style(AnsiCodes.RED, '^', "I don't recognize this")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nI don't recognize this pattern.\n";
        return new Error(
                Error.ErrorType.PARSER_ERROR,
                "syntax error",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getRuntimeArgumentTypeMismatch(FunctionExpression functionExpression, Expression invalidArg, Type expectedType, Type actualType) {
        Annotator annotator = new Annotator(functionExpression.underlyingParseTree.getLine());
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


    public static Error getEmptyLetError(LetExpression letExpression) {
        Annotator annotator = new Annotator(letExpression.underlyingParseTree.getLine());
        /* annotator.applyStyle(
                letExpression.underlyingParseTree,
                // new Annotator.Style(AnsiCodes.RED, '^', null)
        );*/

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nThis let expression has an empty assignment.\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "EMPTY ASSIGNMENT",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getFailedRegularityError(ArrayList<Expression> failedExpressions, ParseTreeNode parseTreeNode) {


        StringBuilder bodyMessage = new StringBuilder();

        bodyMessage.append("You declared the following lambda as regular:\n\n");


        Annotator annotator = new Annotator(parseTreeNode.getLine());

        if (parseTreeNode instanceof NonterminalParseTreeNode) {
            annotator.applyStyle(
                    ((NonterminalParseTreeNode) parseTreeNode).getChildren().get(0),
                    new Annotator.Style(AnsiCodes.RED, '^', null)
            );
        }

        bodyMessage.append(annotator.getAnnotatedString());

        bodyMessage.append("\nBut I can't convert the following expressions to regular functions. They might formally be regular, but they're too complex for me to convert right now.\n");

        for (Expression failedExpression : failedExpressions) {
            Annotator expressionAnnotator = new Annotator(failedExpression.underlyingParseTree.getLine());
            expressionAnnotator.applyStyle(failedExpression.underlyingParseTree, new Annotator.Style(AnsiCodes.RED, '^'));
            bodyMessage.append("\n").append(expressionAnnotator.getAnnotatedString()).append("\n");
        }

        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "Failed Regularity",
                bodyMessage.toString(),
                true,
                0,
                "Try simplifying these expressions or removing the regular declaration"
        );
    }

    public static Error getUnresolvableTypeExpression(TypeExpression expr) {
        Annotator annotator = new Annotator(expr.getExpression().underlyingParseTree.getLine());
        annotator.applyStyle(
                expr.getExpression().underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "I can't resolve this expression")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nI have to know type expressions statically, so that I can check values against them. This expression is too complex to understand statically.\n";

        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "Unresolvable type expression",
                bodyMessage,
                true,
                0,
                "Try simplifying this expression"
        );
    }

    public static Error getWrongArgCountForRegular(ParseTreeNode regularityDeclaration, int argCount) {

        StringBuilder bodyMessage = new StringBuilder();

        bodyMessage.append("You declared the following lambda as regular:\n\n");


        Annotator annotator = new Annotator(regularityDeclaration.getLine());

        if (regularityDeclaration instanceof NonterminalParseTreeNode) {
            annotator.applyStyle(
                    ((NonterminalParseTreeNode) regularityDeclaration).getChildren().get(0),
                    new Annotator.Style(AnsiCodes.RED, '^', null)
            );
        }

        bodyMessage.append(annotator.getAnnotatedString());

        bodyMessage.append("\nBut right now I can only convert lambdas with 1 argument to regular expressions. This one has ").append(argCount).append(".\n");

        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "Failed Regularity",
                bodyMessage.toString(),
                true,
                0,
                "Try simplifying these expressions or removing the regular declaration"
        );
    }

    public static Error getNotStaticArgTypeDeclaration(ParseTreeNode innerExpression) {
        StringBuilder bodyMessage = new StringBuilder();

        Annotator annotator = new Annotator(innerExpression.getLine());

        annotator.applyStyle(innerExpression,
                new Annotator.Style(AnsiCodes.RED, '^', "This argument is not static.")
        );

        bodyMessage.append(annotator.getAnnotatedString());

        bodyMessage.append("\n`type` has to take a statically-known argument. But I can't reduce this argument to a static value.\n");

        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "Failed Static Argument",
                bodyMessage.toString(),
                true,
                0
        );
    }

    public static Error getNotSupportedFunctionTypeDeclaration(ParseTreeNode innerExpression) {
        StringBuilder bodyMessage = new StringBuilder();

        Annotator annotator = new Annotator(innerExpression.getLine());

        annotator.applyStyle(innerExpression,
                new Annotator.Style(AnsiCodes.RED, '^')
        );

        bodyMessage.append(annotator.getAnnotatedString());

        bodyMessage.append("\nI only support lambdas for arguments in `type` right now.\n");

        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "Unsupported Function Type",
                bodyMessage.toString(),
                true,
                0
        );
    }

    public static Error getNotRegularTypeDeclaration(ParseTreeNode innerExpression) {
        StringBuilder bodyMessage = new StringBuilder();

        Annotator annotator = new Annotator(innerExpression.getLine());

        annotator.applyStyle(innerExpression,
                new Annotator.Style(AnsiCodes.RED, '^')
        );

        bodyMessage.append(annotator.getAnnotatedString());

        bodyMessage.append("\n`type` only takes regular arguments, but this argument hasn't been notated as regular.\n");

        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "Regularity Mismatch",
                bodyMessage.toString(),
                true,
                0
        );
    }


    public static Error getTypeStatementTypeMismatch(Expression expr, Type actual) {
        Annotator annotator = new Annotator(expr.underlyingParseTree.getLine());
        annotator.applyStyle(
                expr.underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This has type `"+actual+"`")
        );

        String bodyMessage = annotator.getAnnotatedString()
                + "\n\nThe condition of this type expression has type `"+actual+"`, which doesn't match what I expected, type `Function(Bool, Universe)`.\n";
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "type mismatch",
                bodyMessage,
                true,
                0
        );
    }

    public static Error argumentCountMismatch(FunctionExpression expr, int argCount, int paramCount) {
        Annotator annotator = new Annotator(expr.underlyingParseTree.getLine());
        annotator.applyStyle(
                expr.underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^')
        );

        String bodyMessage = annotator.getAnnotatedString()+"\n\nI was expecting "+paramCount+" argument(s) in this function call, but I got "+argCount;
        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "type mismatch",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getNontypeTypeExpression(TypeExpression typeExpression, Type actualType) {
        Annotator annotator = new Annotator(typeExpression.getExpression().underlyingParseTree.getLine());
        annotator.applyStyle(
                typeExpression.getExpression().underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This has type "+actualType)
        );
        String bodyMessage = annotator.getAnnotatedString()+"\n\nI was expecting this to be a type but it's actually of type "+actualType;

        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "type mismatch",
                bodyMessage,
                true,
                0
        );
    }

    public static Error getNonstaticTypeExpression(TypeExpression typeExpression) {
        Annotator annotator = new Annotator(typeExpression.getExpression().underlyingParseTree.getLine());
        annotator.applyStyle(
                typeExpression.getExpression().underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^')
        );
        String bodyMessage = annotator.getAnnotatedString()+"\n\nI have to be able to evaluate this type statically, but I this is too complex for me to reduce to a static value. ";

        return new Error(
                Error.ErrorType.INTERPRETER_ERROR,
                "type mismatch",
                bodyMessage,
                true,
                0
        );
    }
}
