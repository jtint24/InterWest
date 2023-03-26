package Interpreter;

import ErrorManager.ErrorManager;
import InputBuffer.InputBuffer;
import Lexer.Tokenizer;
import Parser.Parser;

public class InterpretationSession {
    private ErrorManager errorManager;
    private InputBuffer inputBuffer;
    private Tokenizer tokenizer;
    private Parser parser;

    public InterpretationSession() {
        errorManager = new ErrorManager();
        inputBuffer = new InputBuffer("", errorManager);
        tokenizer = new Tokenizer(inputBuffer, errorManager);
    }
}
