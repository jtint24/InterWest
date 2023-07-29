package Tests;

import Interpreter.InterpretationSession;

import java.util.ArrayList;
import java.util.List;

public class Test implements Testable {
    String expectedResult;
    String inputCode;
    TestFunction function;
    int idNumber;

    public Test(String inputCode, String expectedResult, TestFunction function) {
        this.inputCode = inputCode;
        this.expectedResult = expectedResult;
        this.function = function;
    }

    public interface TestFunction {
        Object result(String input);

        TestFunction parser = (String inputCode) -> {
            InterpretationSession sesh = new InterpretationSession(inputCode, true);
            return sesh.testGetParseTree();
        };

        TestFunction lexer = (String inputCode) -> {
            InterpretationSession sesh = new InterpretationSession(inputCode, true);
            return sesh.testGetLexerString();
        };
    }

    @Override
    public List<TestResult> getResults()  {
        return getResults(0);
    }

    @Override
    public List<TestResult> getResults(int level) {
        Object result = function.result(inputCode);
        return new ArrayList<>() {
            {
                add(new TestResult(expectedResult, result.toString(), idNumber+""));
            }
        };
    }


}
