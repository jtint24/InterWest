package Elements;

import Interpreter.*;
import ErrorManager.ErrorManager;
import ErrorManager.Error;
import Parser.ParseTreeNode;
import Regularity.DFA;
import Regularity.DFAConverter;
import Utils.Result;

import java.util.Arrays;
import java.util.Map;

import static ErrorManager.ErrorLibrary.getUnresolvableTypeExpression;
import static ErrorManager.ErrorLibrary.getWrongArgCountForRegular;

public class ExpressionFunction extends Function {
    /**
     * THIS CLASS MAY NEED TO BE DELETED!
     *
     * Frankly, I'm not entirely sure that functions need to be able to wrap expressions, especially if expressions
     * can wrap functions. If you delete this, MAKE SURE to delete the 'state' parameter from the apply function in
     * the base Function class. I don't think there's anywhere else it's needed.
     *
     * Update Apr 3, 2024
     *
     * I now think this is needed. Lambda functions have their functionality depend on expressions. The proper thing
     * to do here, I think, is to have an ExpressionFunction containing all the expressions that make up the lambda.
     * Then that ExpressionFunction is wrapped in an IdentityExpression to allow the lambda to be passed around and
     * used like a value. It may actually be FunctionExpression, which wraps a function in an expression, that may need
     * to be deleted.
     *
     * That being said, I'm eliminating the 'state' parameter from apply anyways. This way, lambdas WILL NOT inherit
     * scope from their context, making them true lambdas and not closures. This will make them referentially
     * transparent and will probably make DFA conversion easier too. It'll make it more of a pain for coders writing
     * in it, but honestly it's a tradeoff I'm willing to make.
     *
     * Update Apr. 4, 2024
     *
     * Here's a problem: Expressions need to be statically checked and validated. Functions don't. But now, since
     * functions can contain expressions, I need to make sure that IdentityExpressions are checking their held values
     * in case they're ExpressionFunctions. This honestly might not be the worst thing since some values might need to
     * be validated statically in the future for other stuff. Anyways, this necessitates adding some kind of validation
     * method. I HATE that this requires using ValidationContext from the Interpreter module, because I'd love to keep
     * the relationship between these two modules unidirectional and not circular, but hey, this already uses
     * Expression, so that's circular.
     *
     * */

    Expression wrappedExpression;
    String[] parameterNames;
    boolean isRegular;
    DFA equivalentDFA = null;
    ParseTreeNode ptNode;

    public ExpressionFunction(FunctionType type, Expression wrappedExpression, String[] parameterNames, boolean isRegular, ParseTreeNode ptNode) {
        this.type = type;
        this.wrappedExpression = wrappedExpression;
        this.parameterNames = parameterNames;
        this.isRegular = isRegular;
        this.ptNode = ptNode;
    }

    @Override
    public Value apply(ErrorManager errorManager, Value... values) {
        validateArguments(errorManager, type, values);

        State state = new State(errorManager);
        for (int i = 0; i<values.length; i++) {
            state.put(parameterNames[i], values[i]);
        }


        ExpressionResult result = wrappedExpression.evaluate(state);

        state.killScope();

        return result.resultingValue;
    }

    static void validateArguments(ErrorManager errorManager, FunctionType type, Value[] values) {
        if (values.length != type.parameterTypes.length) {
            throw new RuntimeException("Expected `"+ type.parameterTypes.length+"` arguments, got `"+values.length+"`");
        }

        for (int i = 0; i<values.length; i++) {
            if (!type.parameterTypes[i].matchesValue(values[i], errorManager)) {

                // TODO: This should be a runtime error, not an exception
                throw new RuntimeException("Type mismatch on parameter "+i+". Value "+values[i]+" doesn't match type "+ type.parameterTypes[i]);
                //errorManager.logError(getRuntimeArgumentTypeMismatch(wrappedExpression, ));
                //new Error(Error.ErrorType.RUNTIME_ERROR, "Type mismatch in parameter "+(i+1)+". Expected "+ type.parameterTypes[i]+", got "+values[i].getType()+".", true));
            }
        }
    }

    public ValidationContext validate(ValidationContext context) {
        context.addScope();
        for (Type paramType : type.parameterTypes) {
            if ((paramType instanceof TypeExpression) && (((TypeExpression) paramType).getExpression() == null || !((TypeExpression) paramType).getStaticValue().isOK())) {
                context.addError(getUnresolvableTypeExpression((TypeExpression) paramType));
            }
        }

        if (
                (type.getResultType() instanceof TypeExpression)
                        && (((TypeExpression) type.getResultType()).getStaticValue() == null
                        || !((TypeExpression) type.getResultType()).getStaticValue().isOK())
        ) {
            context.addError(getUnresolvableTypeExpression((TypeExpression) type.getResultType()));
        }

        if (isRegular) {
            if (parameterNames.length != 1) {
                context.addError(getWrongArgCountForRegular(ptNode, parameterNames.length));
            } else {
                // System.out.println(Arrays.toString(((ReturnableExpressionSeries) wrappedExpression).getContainedExpressions().toArray()));
                Result<DFA, java.util.function.Function<ParseTreeNode, Error>> dfaResult = DFAConverter.dfaFrom(wrappedExpression, parameterNames[0]);
                if (!dfaResult.isOK()) {
                    context.addError(dfaResult.getErrValue().apply(ptNode));
                } else {
                    equivalentDFA = dfaResult.getOkValue();
                }
            }
        }

        for (int i = 0; i<parameterNames.length; i++) {
            String parameterName = parameterNames[i];
            Type parameterType = type.parameterTypes[i];
            context.addVariableType(parameterName, parameterType);
        }

        ValidationContext endContext = wrappedExpression.validate(context);

        // null result type means that it has to be inferred from context
        if (type.resultType == null) {
            type.resultType = wrappedExpression.getType(context);
        }
        endContext.killScope();

        return endContext;
    }

    public StaticReductionContext initializeStaticValues(StaticReductionContext context) {


        for (Type paramType : type.parameterTypes) {
            if (paramType instanceof TypeExpression) {
                ((TypeExpression) paramType).getExpression().initializeStaticValues(context);
            }
        }


        if (type.resultType instanceof TypeExpression) {
            ((TypeExpression) type.resultType).getExpression().initializeStaticValues(context);
        }

        for (int i = 0; i<type.parameterTypes.length; i++) {
            context.constantTypes.put(parameterNames[i], type.parameterTypes[i]);
        }

        context = wrappedExpression.initializeStaticValues(context);
        if (type.getResultType() == null) {
            type.resultType = wrappedExpression.getType(context);
        }

        if (type.getResultType() instanceof TypeExpression) {
            ((TypeExpression) type.getResultType()).getExpression().initializeStaticValues(context);
        }

        return context;
    }

    public Expression getWrappedExpression() {
        return wrappedExpression;
    }

    public String toString() {
        return "{"+String.join(", ", parameterNames)+"->\n"+wrappedExpression.toString()+"}";
    }

    public boolean getIsRegular() {
        return isRegular;
    }
}
