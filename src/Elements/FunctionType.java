package Elements;

import ErrorManager.ErrorManager;

public class FunctionType extends Type {
    Type[] parameterTypes;
    Type resultType;

    public FunctionType(Type resultType, Type... parameterTypes) {
        this.resultType = resultType;
        this.parameterTypes = parameterTypes;
    }

    @Override
    public boolean subtypeOf(Type superType) {
        if (!(superType instanceof FunctionType)) {
            return false;
        }
        FunctionType fSuperType = (FunctionType) superType;
        if (fSuperType.parameterTypes.length != parameterTypes.length) {
            return false;
        }
        if (!resultType.subtypeOf(fSuperType.resultType)) {
            return false;
        }
        for (int i = 0; i<fSuperType.parameterTypes.length; i++) {
            if (!parameterTypes[i].subtypeOf(fSuperType.parameterTypes[i])) {
                return false;
            }
        }
        return true;
    }

    @Override
    public boolean matchesValue(Value v, ErrorManager errorManager) {
        if (!(v instanceof Function)) {
            return false;
        }
        Function func = (Function) v;
        if (func.type.parameterTypes.length != parameterTypes.length) {
            return false;
        }
        if (!resultType.subtypeOf(func.type.resultType)) {
            return false;
        }
        for (int i = 0; i<func.type.parameterTypes.length; i++) {
            if (!parameterTypes[i].subtypeOf(func.type.parameterTypes[i])) {
                return false;
            }
        }
        return true;
    }
}
