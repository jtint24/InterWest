package ErrorManager;

import java.util.ArrayList;
import java.util.List;

public class ErrorManager {
    ArrayList<Error> errors = new ArrayList<>();

    public void logError(Error e) {
        errors.add(e);
        if (e.getIsFatal()) {
            killSession();
        }
    }

    public void logErrors(List<Error> newErrors) {

        errors.addAll(newErrors);

        for (Error error : newErrors) {
            if (error.getIsFatal()) {
                killSession();
            }
        }
    }

    public void killSession() {
        printErrors();
        System.exit(0);
    }

    public void printErrors() {
        for (Error error : errors) {
            System.out.println(error);
            new Exception().printStackTrace();
            System.out.println();
        }
    }
}
