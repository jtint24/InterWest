package Interpreter;

import Elements.Type;
import Elements.Value;
import ErrorManager.Error;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Parser.ParseTreeNode;
import Utils.Result;
import Utils.TriValue;

public abstract class Expression {
    Result<Value, Error> staticValue;
    public ParseTreeNode underlyingParseTree;
    public abstract ExpressionResult evaluate(State situatedState);
    public abstract ValidationContext validate(ValidationContext context);

    /**
     * getType
     *
     * Gets the result type of the expression, except in case of early return (in which case the result type of the
     * expression may not match this type)
     * */
    public abstract Type getType(ValidationContext context);

    /**
     * reduceToValue
     *
     * Give the static result of the expression, if it exists
     * */
    public Result<Value, Error> reduceToValue() {
        return staticValue;
    }

    public abstract StaticReductionContext initializeStaticValues(StaticReductionContext context);

    public TriValue matchesType(Type type, ValidationContext context) {
        TriValue subtypeStatus = type.subtypeOf(getType(context));

        if (subtypeStatus != TriValue.TRUE && this.staticValue.isOK()) {
            Value inputValue = this.staticValue.getOkValue();
            ErrorManager testErrorManager = new ErrorManager(new OutputBuffer());
            subtypeStatus = TriValue.fromBool(type.matchesValue(inputValue, testErrorManager));
            context.addErrors(testErrorManager.getErrors());
        }

        return subtypeStatus;
    }
}
