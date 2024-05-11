package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.Error;
import Parser.ParseTreeNode;
import Utils.Result;
import Utils.TriValue;

import static ErrorManager.ErrorLibrary.*;

public class ReturnExpression extends Expression {

    Expression exprToReturn;

    public ReturnExpression(Expression exprToReturn, ParseTreeNode underlyingParseTree) {
        this.exprToReturn = exprToReturn;
        this.underlyingParseTree = underlyingParseTree;
    }

    public Expression getExprToReturn() {
        return exprToReturn;
    }

    @Override
    public ExpressionResult evaluate(State situatedState) {
        ExpressionResult returnResult = exprToReturn.evaluate(situatedState);
        returnResult.setEarlyReturn(true);
        return returnResult;
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        context = exprToReturn.validate(context);

        if (context.getReturnType() == null) {
            context.addError(getNotReturnable(this));
        } else {
            Type actualReturnType = exprToReturn.getType(context);

            TriValue subtypeStatus = exprToReturn.matchesType(context.getReturnType(), context);
            if (subtypeStatus == TriValue.FALSE) {
                context.addError(getReturnTypeMismatch(this, context.getReturnType(), exprToReturn.getType(context)));
            } else if (subtypeStatus == TriValue.UNKNOWN) {
                context.addError(getReturnTypeWarning(this, context.getReturnType(), exprToReturn.getType(context)));
            }
        }
        return context;
    }

    @Override
    public Type getType(ValidationContext context) {
        return ValueLibrary.boolType;
    }


    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        StaticReductionContext discardedContext = exprToReturn.initializeStaticValues(context);

        context.returnedValue = exprToReturn.staticValue;

        return context;
    }

    @Override
    public String toString() {
        String line = "return "+exprToReturn;
        if (!line.endsWith("\n")) {
            line += "\n";
        }
        return line;
    }
}
