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
            errorManager.logError(new Error(Error.ErrorType.RUNTIME_ERROR, "Scope already contains `"+id+"`!", true));
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

        errorManager.logError(new Error(Error.ErrorType.RUNTIME_ERROR, "Can't find value `"+id+"` in scope!", true));
        return null;
    }

    public String toString() {
        String retString = "";

        Iterator<HashMap<String, Value>> iterator = scopes.descendingIterator();
        while (iterator.hasNext()) {
            HashMap<String, Value> scope = iterator.next();
            retString += "------\n";
            for (Map.Entry<String, Value> variable : scope.entrySet()) {
                retString += variable.getKey()+": "+variable.getValue() + "\n";
            }
        }

        return retString;
    }


    public void killScope() {
        scopes.pop();
    }
}
