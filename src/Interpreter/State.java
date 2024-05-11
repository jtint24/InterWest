package Interpreter;

import Elements.Value;
import Elements.ValueLibrary;
import ErrorManager.Error;
import ErrorManager.ErrorManager;

import java.util.*;

public class State {
    ArrayDeque<HashMap<String, Value>> scopes = new ArrayDeque<>();
    ErrorManager errorManager;

    public State(ErrorManager errorManager) {
        this.errorManager = errorManager;

        scopes.push(ValueLibrary.builtinValues);
    }

    public void addScope() {
        scopes.push(new HashMap<>());
    }

    public void put(String id, Value val) {
        if (contains(id)) {
            // errorManager.logError(new Error(Error.ErrorType.RUNTIME_ERROR, "Scope already contains `"+id+"`!", true));
            throw new RuntimeException("Scope already contains `"+id+"`!");
        }

        scopes.peek().put(id, val);
    }

    public boolean contains(String id) {
        Iterator<HashMap<String, Value>> iterator = scopes.descendingIterator();
        while (iterator.hasNext()) {
            HashMap<String, Value> scope = iterator.next();
            if (scope.containsKey(id)) {
                return true;
            }
        }
        return false;
    }

    public Value get(String id) {
        Iterator<HashMap<String, Value>> iterator = scopes.descendingIterator();
        while (iterator.hasNext()) {
            HashMap<String, Value> scope = iterator.next();
            if (scope.containsKey(id)) {
                return scope.get(id);
            }
        }

        throw new RuntimeException("Can't find value `"+id+"` in scope!");
        // errorManager.logError(new Error(Error.ErrorType.RUNTIME_ERROR, "Can't find value `"+id+"` in scope!", true));
        // return null;
    }

    public String toString() {
        StringBuilder retString = new StringBuilder();

        Iterator<HashMap<String, Value>> iterator = scopes.descendingIterator();
        while (iterator.hasNext()) {
            HashMap<String, Value> scope = iterator.next();
            retString.append("------\n");
            for (Map.Entry<String, Value> variable : scope.entrySet()) {
                retString.append(variable.getKey()).append(": ").append(variable.getValue()).append("\n");
            }
        }

        return retString.toString();
    }


    public void killScope() {
        scopes.pop();
    }
}
