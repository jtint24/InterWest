package Tests;

public class TestResult {
    String expected;
    String result;
    String origin;


    public TestResult(String expected, String result, String origin) {
        this.expected = expected;
        this.result = result;
        this.origin = origin;
    }

    boolean isPassed() {
        return result.equals(expected);
    }

    void appendOrigin(String name) {
        origin = name+"."+origin;
    }

    String differenceString() {
        if (isPassed()) {
            return "passed!";
        }
        return "EXPECTED:\n" + expected + "\nRESULT:\n" + result;
    }

    @Override
    public String toString() {
        return origin+"\n"+differenceString();
    }
}
