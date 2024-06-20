package Interpreter;

import Elements.Type;
import Elements.Value;
import Utils.Result;
import ErrorManager.Error;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;

public class StaticReductionContext {
    HashMap<String, Value> declaredConstants = new HashMap<>();
    public HashMap<String, Type> constantTypes = new HashMap<>();
    ArrayList<Error> errors = new ArrayList<>();
    Result<Value, String> returnedValue = null;

    public void addErrors(ArrayList<Error> newErrors) {
        errors.addAll(newErrors);
    }

    public void addError(Error newError) {
        errors.add(newError);
    }


}
