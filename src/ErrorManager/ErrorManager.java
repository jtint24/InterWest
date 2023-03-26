package ErrorManager;

import java.util.ArrayList;

public class ErrorManager {
    ArrayList<Error> errors = new ArrayList<>();

    public void logError(Error e) {
        if (e.getIsFatal()) {
            System.out.println("Fatal interpreter error.");
            System.out.println(e);
            new Exception().printStackTrace();
            System.exit(0);
        }
    }
}
