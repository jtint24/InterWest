package Elements;

import ErrorManager.ErrorManager;
import Interpreter.Expression;
import Interpreter.StaticReductionContext;
import Interpreter.ValidationContext;
import Utils.Result;
import Utils.TriValue;
import static ErrorManager.ErrorLibrary.getNontypeTypeExpression;
import static ErrorManager.ErrorLibrary.getNonstaticTypeExpression;


public class TypeExpression extends Type implements Evaluatable {

    // An expression that resolves to a type

    Expression definition;

    Result<Type, String> staticValue = Result.error("Not initialized");

    public TypeExpression(Expression definition) {
        this.definition = definition;
    }

    @Override
    public TriValue subtypeOf(Type superType) {
        if (staticValue.isOK()) {
            return staticValue.getOkValue().subtypeOf(superType);
        }
        return TriValue.UNKNOWN;
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        if (staticValue.isOK()) {
            return staticValue.getOkValue().matchesValue(v, errorManager);
        }
        return false;
    }

    public Expression getExpression() {
        return definition;
    }


    public ValidationContext validate(ValidationContext context) {
        definition.validate(context);
        return context;
    }

    @Override
    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {
        context = definition.initializeStaticValues(context);

        if (!definition.staticValue.isOK()) {
            context.addError(getNonstaticTypeExpression(this));
            staticValue = Result.error("Invalid");
        } else {
            if (!(definition.staticValue.getOkValue() instanceof Type)) {
                context.addError(getNontypeTypeExpression(this, definition.staticValue.getOkValue().getType()));
                staticValue = Result.error("Invalid");
            } else {
                staticValue = Result.ok((Type) definition.staticValue.getOkValue());
            }
        }

        return context;
    }

    public Result<Type, String> getStaticValue() {
        if (!staticValue.isOK() && staticValue.getErrValue().equals("Not initialized")) {
            initializeStaticValues(new StaticReductionContext());
        }
        return staticValue;
    }

    @Override
    public String toString() {
        return getExpression().toString();
    }
}
