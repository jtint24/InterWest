package Elements;

import ErrorManager.Error;
import ErrorManager.ErrorManager;
import Interpreter.Expression;
import Utils.Result;
import Utils.TriValue;

public class TypeExpression extends Type {

    // An expression that resolves to a type

    Expression definition;
    Result<Value, Error> staticValue;

    public TypeExpression(Expression definition) {
        this.definition = definition;
        this.staticValue = definition.reduceToValue();
    }

    @Override
    public TriValue subtypeOf(Type superType) {

        return TriValue.UNKNOWN;
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        return false;
    }
}
