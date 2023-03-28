import Interpreter.InterpretationSession;

public class Main {
    public static void main(String[] args) {

        InterpretationSession newSession = new InterpretationSession("123+321");

        newSession.runSession();


    }
}