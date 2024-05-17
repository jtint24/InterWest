package IO;


import ErrorManager.ErrorManager;

public class InputBuffer {
    private ErrorManager errorManager;

    private String body;
    public InputBuffer(String body, ErrorManager errorManager) {
        this.body = body;
        this.errorManager = errorManager;
    }
    public char getChar() {
        if (this.isTerminated()) {
            throw new RuntimeException("InputBuffer has run out");
            // errorManager.logError(new Error(Error.ErrorType.INPUT_ERROR, "InputBuffer has run out", false));
        }
        char retChar = body.charAt(0);
        body = body.substring(1);
        return retChar;
    }

    public boolean isTerminated() {
        return body.isEmpty();
    }

    public void ungetChar(char c) {
        body = c + body;
    }
}
