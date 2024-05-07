package Interpreter;

import Elements.Value;
import Utils.Result;
import ErrorManager.Error;

import java.util.ArrayList;
import java.util.HashMap;

public class StaticReductionContext {
    HashMap<String, Value> declaredConstants;
    ArrayList<Error> errors;
    Result<Value, Error> returnedValue;

    public void addErrors(ArrayList<Error> newErrors) {
        errors.addAll(newErrors);
    }


}
