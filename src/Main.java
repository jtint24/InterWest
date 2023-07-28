import Interpreter.InterpretationSession;

public class Main {
    public static void main(String[] args) {

        InterpretationSession newSession = new InterpretationSession("let a = 5 \nlet b = a \nlet c = a(1,2,\"hello\")\n let d = wep(a) let e = a(b(c(d(e))))");

        newSession.runSession();

    }
}