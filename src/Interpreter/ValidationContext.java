package Interpreter;

import Elements.Type;
import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.Error;

import java.util.*;

public class ValidationContext {
    ArrayDeque<HashMap<String, Type>> constantTypes = new ArrayDeque<>();
    public ArrayList<Error> errors = new ArrayList<>();
    public HashMap<String, Type> forwardDeclaredConstants = new HashMap<>();


    Type returnType;

    public ValidationContext() {

        constantTypes.push(new HashMap<>());

        for (Map.Entry<String, Value> variable : ValueLibrary.builtinValues.entrySet()) {
            constantTypes.peek().put(variable.getKey(), variable.getValue().getType());
        }
    }

    public void addVariableType(String identifierName, Type type) {
        assert constantTypes.peek() != null;
        constantTypes.peek().put(identifierName, type);
    }

    public void addVariableTypes(HashMap<String, Type> forwardDeclaredTypes) {
        assert constantTypes.peek() != null;
        constantTypes.peek().putAll(forwardDeclaredTypes);
    }

    public boolean hasVariable(String identifierName) {
        Iterator<HashMap<String, Type>> iterator = constantTypes.descendingIterator();

        while (iterator.hasNext()) {
            HashMap<String, Type> scope = iterator.next();
            if (scope.containsKey(identifierName)) {
                return true;
            }
        }

        return false;
    }


    public Type getVariableType(String identifierName) {
        Iterator<HashMap<String, Type>> iterator = constantTypes.descendingIterator();

        while (iterator.hasNext()) {
            HashMap<String, Type> scope = iterator.next();
            if (scope.containsKey(identifierName)) {
                return scope.get(identifierName);
            }
        }

        return null; // Log an error here? It means we can't find a variable in scope
    }

    public void addScope() {
        constantTypes.addFirst(new HashMap<>());
    }

    public void killScope() {
        constantTypes.pop();
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
