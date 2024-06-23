package Interpreter;

import Elements.*;
import ErrorManager.Error;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Parser.ParseTreeNode;
import Utils.Result;
import Utils.TriValue;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public abstract class Expression implements Evaluatable {
    public Result<Value, String> staticValue;
    public ParseTreeNode underlyingParseTree;
    public abstract ExpressionResult evaluate(State situatedState);
    /**
     * getType
     *
     * Gets the result type of the expression, except in case of early return (in which case the result type of the
     * expression may not match this type)
     * */
    public abstract Type getType(ValidationContext context);
    public abstract Type getType(StaticReductionContext context);


    /**
     * reduceToValue
     *
     * Give the static result of the expression, if it exists
     * */
    public Result<Value, String> reduceToValue() {
        return staticValue;
    }
    public TriValue matchesType(Type type, ValidationContext context) {
        TriValue subtypeStatus;

        if (this.staticValue.isOK()) {
            Value inputValue = this.staticValue.getOkValue();

            ErrorManager testErrorManager = new ErrorManager(new OutputBuffer());
            subtypeStatus = TriValue.fromBool(type.matchesValue(inputValue, testErrorManager));

            context.addErrors(testErrorManager.getErrors());

            return subtypeStatus;
        }

        subtypeStatus = getType(context).subtypeOf(type);

        return subtypeStatus;
    }

    protected Map<String, Type> validateForwardDeclaration(ValidationContext context) {
        return new HashMap<>();
    }
}
