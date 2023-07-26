import Interpreter.InterpretationSession;

public class Main {
    public static void main(String[] args) {

        InterpretationSession newSession = new InterpretationSession("let a = 5 let b=7 balawef=");

        newSession.runSession();


    }
}