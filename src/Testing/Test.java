package Testing;

import Elements.Value;
import Elements.ValueLibrary;
import Elements.ValueWrapper;
import ErrorManager.ErrorManager;
import IO.OutputBuffer;
import Interpreter.TestInterpretationSession;
import Regularity.DFA;
import Regularity.DFAConditions;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
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

        this.function = switch (funcLine) {
            case "lexer" -> TestFunction.lexer;
            case "parser" -> TestFunction.parser;
            case "interpreter" -> TestFunction.interpreter;
            case "execution" -> TestFunction.execution;
            case "condition dfa" ->  TestFunction.conditionDFA;
            case "dfa conversion" -> TestFunction.dfaConversion;
            default -> null;
        };
        if (function == null) {
            throw new RuntimeException("Unrecognized test type `"+funcLine+"`");
        }

        while(sc.hasNextLine()) {
            String line = sc.nextLine();
            if (line.equals("EXPECTED_RESULT:")) {
                onInput = false;
                continue;
            }
            if (onInput) {
                inputCode.append(line).append("\n");
            } else {
                expectedResult.append(line).append("\n");
            }
        }

        this.expectedResult = expectedResult.toString();
        this.inputCode = inputCode.toString();
    }
    public interface TestFunction {
        Object result(String input);

        TestFunction parser = (String inputCode) -> {
            TestInterpretationSession sesh = new TestInterpretationSession(inputCode);
            return sesh.testGetParseTree();
        };

        TestFunction lexer = (String inputCode) -> {
            TestInterpretationSession sesh = new TestInterpretationSession(inputCode);
            return sesh.testGetLexerString();
        };
        TestFunction interpreter = (String inputCode) -> {
            TestInterpretationSession sesh = new TestInterpretationSession(inputCode);
            return sesh.testGetInterpretation();
        };
        TestFunction dfaConversion = (String inputCode) -> {
            TestInterpretationSession sesh = new TestInterpretationSession(inputCode);
            return sesh.testDFAConversion();
        };
        TestFunction execution = (String inputCode) -> {
            TestInterpretationSession sesh = new TestInterpretationSession(inputCode);
            return sesh.testExecution();
        };
        TestFunction conditionDFA = (String inputCode) -> {
            String[] args = inputCode.trim().split(" ");
            String rawValue = args[0];
            String typeStr = args[1];
            String conditionStr = args[2];
            String compare = args[3];

            Value val;
            Value compVal;
            switch (typeStr) {
                case "int" -> {
                    val = new ValueWrapper<>(Integer.parseInt(rawValue), ValueLibrary.intType);
                    compVal = new ValueWrapper<>(Integer.parseInt(compare), ValueLibrary.intType);
                }
                case "bool" -> {
                    val = new ValueWrapper<>(Boolean.valueOf(rawValue), ValueLibrary.boolType);
                    compVal = new ValueWrapper<>(Boolean.valueOf(compare), ValueLibrary.boolType);
                }
                default -> {
                    throw new IllegalArgumentException(typeStr+" is not a valid type");
                }
            }


            DFA dfa;

            switch (conditionStr) {
                case "=" -> dfa = DFAConditions.dfaEqualTo(val);
                case "!=" -> dfa = DFAConditions.dfaInequalTo(val);
                default -> throw new IllegalArgumentException(conditionStr+" is not a valid condition");
            }

            boolean result = dfa.getResult(compVal.toBoolString(), new ErrorManager(new OutputBuffer())) == ValueLibrary.trueValue;

            return dfa+"\nDid "+compare+" pass "+conditionStr+rawValue+"? "+result+"\n";
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
