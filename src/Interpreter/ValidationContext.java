package Interpreter;

import Elements.Type;
import ErrorManager.Error;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class ValidationContext {
    ArrayDeque<HashMap<String, Type>> variableTypes = new ArrayDeque<>();
    ArrayList<Error> errors = new ArrayList<>();

    public ValidationContext() {
        variableTypes.push(new HashMap<>());
    }

    public void addVariableType(String identifierName, Type type) {
        assert variableTypes.peek() != null;
        variableTypes.peek().put(identifierName, type);
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
}
