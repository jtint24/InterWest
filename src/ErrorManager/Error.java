package ErrorManager;

public class Error {
    private final int errorLevel;
    private final String annotation;
    private final ErrorType type;
    private final boolean isFatal;

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
        INPUT_ERROR,
        PARSER_ERROR,
        INTERPRETER_ERROR,
        RUNTIME_ERROR
    }

    @Override
    public String toString() {
        return type.toString() + "\n" + annotation;
    }
}
