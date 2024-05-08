package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.Error;

import java.util.*;

public class ValidationContext {
    ArrayDeque<HashMap<String, Type>> variableTypes = new ArrayDeque<>();
    public ArrayList<Error> errors = new ArrayList<>();

    Type returnType;

    public ValidationContext() {

        variableTypes.push(new HashMap<>());

        for (Map.Entry<String, Value> variable : ValueLibrary.builtinValues.entrySet()) {
            variableTypes.peek().put(variable.getKey(), variable.getValue().getType());
        }
    }

    public void addVariableType(String identifierName, Type type) {
        assert variableTypes.peek() != null;
        variableTypes.peek().put(identifierName, type);
    }

    public boolean hasVariable(String identifierName) {
        Iterator<HashMap<String, Type>> iterator = variableTypes.descendingIterator();

        while (iterator.hasNext()) {
            HashMap<String, Type> scope = iterator.next();
            if (scope.containsKey(identifierName)) {
                return true;
            }
        }

        return false;
    }


    public Type getVariableType(String identifierName) {
        Iterator<HashMap<String, Type>> iterator = variableTypes.descendingIterator();

        while (iterator.hasNext()) {
            HashMap<String, Type> scope = iterator.next();
            if (scope.containsKey(identifierName)) {
                return scope.get(identifierName);
            }
        }

        return null; // Log an error here? It means we can't find a variable in scope
    }

    public void addScope() {
        variableTypes.add(new HashMap<>());
    }

    public void killScope() {
        variableTypes.pop();
    }

    public void addError(Error err) {
        errors.add(err);
    }

    public Type getReturnType() {
        return returnType;
    }
    public void setReturnType(Type type) {
        returnType = type;
    }

    public void addErrors(List<Error> newErrors) {
        errors.addAll(newErrors);
    }
}
