package Testing;

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
        String ansiCleanedResult = result.replaceAll("(\\x9B|\\x1B\\[)[0-?]*[ -\\/]*[@-~]", "");
        String ansiCleanedExpected = expected.replaceAll("(\\x9B|\\x1B\\[)[0-?]*[ -\\/]*[@-~]", "");
        return ansiCleanedResult.equals(ansiCleanedExpected);
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
