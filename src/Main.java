import Interpreter.InterpretationSession;

public class Main {
    public static void main(String[] args) {

        InterpretationSession newSession = new InterpretationSession("1+2+3+4");

        newSession.runSession();


    }
}