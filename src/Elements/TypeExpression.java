package Elements;

import ErrorManager.ErrorManager;
import Interpreter.Expression;
import Utils.Result;

public class TypeExpression extends Type {

    // An that resolves to a type

    Expression definition;
    Result<Value, Exception> staticValue;

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
