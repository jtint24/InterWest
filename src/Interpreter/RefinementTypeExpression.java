package Interpreter;

import Elements.*;
import Parser.ParseTreeNode;
import Utils.Result;

import static ErrorManager.ErrorLibrary.*;

public class RefinementTypeExpression extends Expression {

    Expression innerExpression;

    public RefinementTypeExpression(Expression innerExpression, ParseTreeNode parseTreeNode) {
        this.innerExpression = innerExpression;
        this.underlyingParseTree = parseTreeNode;
    }


    @Override
    public ExpressionResult evaluate(State situatedState) {
        return new ExpressionResult(situatedState, this.staticValue.getOkValue());
    }

    @Override
    public ValidationContext validate(ValidationContext context) {
        return innerExpression.validate(context);
    }

    @Override
    public Type getType(ValidationContext context) {
        return ValueLibrary.typeType;
    }
    @Override
    public Type getType(StaticReductionContext context) {
        return ValueLibrary.typeType;
    }

    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {

        StaticReductionContext wrappedContext = innerExpression.initializeStaticValues(context);

        this.staticValue = Result.error("Type can't be made from inner value");
        if (innerExpression.staticValue.isOK()) {
            if (innerExpression.staticValue.getOkValue() instanceof ExpressionFunction) {
                ExpressionFunction expressFunc = (ExpressionFunction) innerExpression.staticValue.getOkValue();
                if (expressFunc.getIsRegular()) {

                    FunctionType fType = expressFunc.getType();

                    FunctionType expectedType = new FunctionType(ValueLibrary.boolType, ValueLibrary.universeType);
                    if (fType.subtypeOf(expectedType).unknownIsFalse()) {
                        Type parentType = fType.getParameterTypes()[0];
                        this.staticValue = Result.ok(new RefinementType(parentType, expressFunc));
                    } else {
                        context.addError(getTypeStatementTypeMismatch(innerExpression, fType));
                    }
                } else {
                    context.addError(getNotRegularTypeDeclaration(innerExpression.underlyingParseTree));
                }
            } else {
                context.addError(getNotSupportedFunctionTypeDeclaration(innerExpression.underlyingParseTree));
            }
        } else {
            context.addError(getNotStaticArgTypeDeclaration(innerExpression.underlyingParseTree));
        }

        return wrappedContext;
    }
}
