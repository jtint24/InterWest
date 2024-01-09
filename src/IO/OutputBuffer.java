package IO;
import java.io.StringWriter;
import java.io.PrintWriter;

public class OutputBuffer {
    boolean silence = false;
    public boolean terse = false; // If true, won't print error stack traces
    String output = "";

    public OutputBuffer() {}

    public OutputBuffer(boolean terse) {
        this.terse = terse;
    }

    public void printStackTrace() {
        StringWriter sw = new StringWriter();
        PrintWriter pw = new PrintWriter(sw);
        if (!terse) {
            new Exception().printStackTrace(pw);
        }
        output += sw.toString();

        if (!silence) {
            new Exception().printStackTrace();
        }
    }
    public void println() {
        println("");
    }
    public void println(Object x) {
        output += x + "\n";
        if (!silence) {
            System.out.println(x);
        }
    }
    public void print() {
        print("");
    }
    public void print(Object x) {
        output += x;
        if (!silence) {
            System.out.print(x);
        }
    }

    @Override
    public String toString() {
        return output;
    }

    public void silence() {
        silence = true;
    }
    public void unsilence() {
        silence = false;
    }

}
