package ErrorManager;

import Elements.Type;
import Interpreter.ConditionalExpression;

public class ErrorLibrary {
    public static Error getIfTypeMismatch(ConditionalExpression conditionalExpression, Type actual) {
        Annotator annotator = new Annotator(conditionalExpression.underlyingParseTree);
        annotator.applyStyle(
                conditionalExpression.getCondition().underlyingParseTree,
                new Annotator.Style(AnsiCodes.RED, '^', "This looks like it has type `"+actual+"`")
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
                new Annotator.Style(AnsiCodes.RED, '^', "This looks like it has type `"+actual+"`")
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




}
