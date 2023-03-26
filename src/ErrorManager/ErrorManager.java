package ErrorManager;

import java.util.ArrayList;

public class ErrorManager {
    ArrayList<Error> errors = new ArrayList<>();

    public void logError(Error e) {
        if (e.getIsFatal()) {
            System.out.println(e);
            System.exit(0);
        }
    }
}
