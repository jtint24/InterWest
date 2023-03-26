package ErrorManager;

public class Error {
    private int errorLevel;
    private String annotation;
    private ErrorType type;
    private boolean isFatal;

    public Error(ErrorType type, String annotation, boolean isFatal) {
        this.annotation = annotation;
        this.type = type;
        this.errorLevel = 0;
        this.isFatal = isFatal;
    }

    public Error(ErrorType type, String annotation, boolean isFatal, int errorLevel) {
        this.annotation = annotation;
        this.type = type;
        this.errorLevel = errorLevel;
        this.isFatal = isFatal;
    }

    public boolean getIsFatal() {
        return isFatal;
    }

    public enum ErrorType {
        LEXER_ERROR,
        INPUT_ERROR
    }

    @Override
    public String toString() {
        return type.toString() + "\n" + annotation;
    }
}
