package Elements;

import ErrorManager.ErrorManager;
import Utils.TriValue;

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

        if (resultType.subtypeOf(fSuperType.resultType) != TriValue.TRUE) {
            return resultType.subtypeOf(fSuperType.resultType);
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
}
