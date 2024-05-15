package ErrorManager;

import java.util.Locale;

public class Error {
    private final String header;
    private final String body;
    private final ErrorType type;
    private final boolean isFatal;
    private final int errorCode;
    private final String[] helpItems;

    public Error(ErrorType type, String header, String body, boolean isFatal, int errorCode, String... helpItems) {
        this.type = type;
        this.header = header;
        this.body = body;
        this.isFatal = isFatal;
        this.errorCode = errorCode;
        this.helpItems = helpItems;
    }
    public boolean getIsFatal() {
        return isFatal;
    }

    public enum ErrorType {
        LEXER_ERROR('L'),
        INPUT_ERROR('I'),
        PARSER_ERROR('P'),
        INTERPRETER_ERROR('A'),
        RUNTIME_ERROR('R');
        public final char code;
        ErrorType(char c) {
            code = c;
        }
    }

    @Override
    public String toString() {
        String headerColor = this.isFatal ? AnsiCodes.RED + "[X]" : AnsiCodes.YELLOW + "[!]";
        String dividerLine = headerColor + " " + header.toUpperCase(Locale.ROOT) + " " + "-".repeat(80-5-5-header.length()) + " " + type.code + String.format("%03d", errorCode) + AnsiCodes.RESET;
        // String headerLine = header.toUpperCase(Locale.ROOT);
        String helpString;

        if (helpItems.length > 0) {
            helpString = "help: " + String.join("\nhelp: ", helpItems);
        } else {
            helpString = "";
        }


        return dividerLine+"\n"+""+"\n"+body+"\n"+helpString;
    }
}
