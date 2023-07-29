package ErrorManager;

import IO.OutputBuffer;

import java.util.ArrayList;
import java.util.List;

public class ErrorManager {
    ArrayList<Error> errors = new ArrayList<>();
    boolean suppress = false;
    OutputBuffer outputBuffer;

    public ErrorManager(OutputBuffer outputBuffer) {
        this(outputBuffer, false);
    }
    public ErrorManager(OutputBuffer outputBuffer, boolean suppress) {
        this.outputBuffer = outputBuffer;
        this.suppress = suppress;
    }

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

    public boolean hasErrors() {
        return errors.size() > 0;
    }

    public void killSession() {
        printErrors();
        // System.exit(0);
        throw new RuntimeException();
    }

    public void printErrors() {
        if (suppress) {
            return;
        }
        for (Error error : errors) {
            outputBuffer.println(error);
            outputBuffer.printStackTrace();
            outputBuffer.println();
        }
    }
}
