package Testing;

import Interpreter.InterpretationSession;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Test implements Testable {
    String expectedResult;
    String inputCode;
    TestFunction function;
    String name;

    public Test(String inputCode, String expectedResult, TestFunction function) {
        this.inputCode = inputCode;
        this.expectedResult = expectedResult;
        this.function = function;
    }

    public Test(File file) throws FileNotFoundException {
        String name = file.getName();
        name = name.substring(0, name.indexOf('.'));
        this.name = name;

        StringBuilder inputCode = new StringBuilder();
        StringBuilder expectedResult = new StringBuilder();

        boolean onInput = true;

        Scanner sc = new Scanner(file);

        String funcLine = sc.nextLine();

        switch (funcLine) {
            case "lexer" -> {
                this.function = TestFunction.lexer;
            }
            case "parser" -> {
                this.function = TestFunction.parser;
            }
        }

        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.equals("EXPECTED_RESULT:")) {
                onInput = false;
                continue;
            }
            if (onInput) {
                inputCode.append(line+"\n");
            } else {
                expectedResult.append(line+"\n");
            }
        }

        this.expectedResult = expectedResult.toString();
        this.inputCode = inputCode.toString();
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
                add(new TestResult(expectedResult, result.toString(), name));
            }
        };
    }


}
