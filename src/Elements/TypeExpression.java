package Elements;

import ErrorManager.Error;
import ErrorManager.ErrorManager;
import Interpreter.Expression;
import Utils.Result;
import Utils.TriValue;

public class TypeExpression extends Type {

    // An expression that resolves to a type

    Expression definition;

    public TypeExpression(Expression definition) {
        this.definition = definition;
    }

    @Override
    public TriValue subtypeOf(Type superType) {
        // TODO: use static value
        Result<Type, String> staticValue = getStaticValue();
        if (staticValue.isOK()) {
            return staticValue.getOkValue().subtypeOf(superType);
        }
        return TriValue.UNKNOWN;
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        return false;
    }

    public Expression getExpression() {
        return definition;
    }

    public Result<Type, String> getStaticValue() {
        Result<Value, String> staticValue = getExpression().staticValue;
        if (staticValue.isOK()) {
            assert staticValue.getOkValue() instanceof Type;
            return Result.ok((Type) staticValue.getOkValue());
        } else {
            return Result.error(staticValue.getErrValue());
        }
    }

    @Override
    public String toString() {
        return getExpression().toString();
    }
}
