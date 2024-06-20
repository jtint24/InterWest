package Elements;

import ErrorManager.ErrorManager;
import Utils.TriValue;

import java.util.Arrays;
import java.util.stream.Collectors;

public class FunctionType extends Type {
    Type[] parameterTypes;
    Type resultType;

    public FunctionType(Type resultType, Type... parameterTypes) {
        this.resultType = resultType;
        this.parameterTypes = parameterTypes;
    }

    @Override
    public TriValue subtypeOf(Type superType) {
        if (!(superType instanceof FunctionType)) {
            return TriValue.FALSE;
        }

        FunctionType fSuperType = (FunctionType) superType;
        if (fSuperType.parameterTypes.length != parameterTypes.length) {
            return TriValue.FALSE;
        }

        TriValue resultTypeSubtypeValue = resultType.subtypeOf(fSuperType.resultType);

        if (resultTypeSubtypeValue != TriValue.TRUE) {
            return resultTypeSubtypeValue;
        }

        for (int i = 0; i<fSuperType.parameterTypes.length; i++) {
            TriValue subTypeStatus = parameterTypes[i].subtypeOf(fSuperType.parameterTypes[i]);
            if (subTypeStatus != TriValue.TRUE) {
                return subTypeStatus;
            }
        }

        return TriValue.TRUE;
    }

    public Type getResultType() {
        return resultType;
    }
    public Type[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        if (!(v instanceof Function)) {
            return false;
        }
        Function func = (Function) v;
        return this.subtypeOf(func.getType()).unknownIsFalse();
    }

    @Override
    public String toString() {
        return "Function("+resultType+"; "+Arrays.stream(parameterTypes).map(Type::toString).collect(Collectors.joining(", "))+")";

    }
}
