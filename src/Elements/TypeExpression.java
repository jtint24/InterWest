package Elements;

import ErrorManager.Error;
import ErrorManager.ErrorManager;
import Interpreter.Expression;
import Utils.Result;

public class TypeExpression extends Type {

    // An expression that resolves to a type

    Expression definition;
    Result<Value, Error> staticValue;

    public TypeExpression(Expression definition) {
        this.definition = definition;
        this.staticValue = definition.reduceToValue();
    }

    @Override
    public boolean subtypeOf(Type superType) {

        return false;
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        return false;
    }
}
