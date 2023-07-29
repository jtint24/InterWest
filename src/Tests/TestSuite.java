package Tests;

import java.util.ArrayList;
import java.util.List;

public class TestSuite implements Testable {
    Testable[] subTests;
    String name;

    public TestSuite(String name, Testable[] subTests) {
        this.name = name;
        this.subTests = subTests;

        int count = 0;
        for (Testable subTest : subTests) {
            if (subTest instanceof Test) {
                count++;
                ((Test) subTest).idNumber = count;
            }
        }
    }

    @Override
    public List<TestResult> getResults() {
        return getResults(0);
    }

    @Override
    public List<TestResult> getResults(int level) {
        ArrayList<TestResult> testResults = new ArrayList<>();
        for (Testable test : subTests) {
            testResults.addAll(test.getResults(level+1));
        }

        int successfulTests = 0;

        for (TestResult testResult : testResults) {
            if (testResult.isPassed()) {
                successfulTests += 1;
            }
        }

        System.out.print("\t".repeat(level)+" - "+name+" ");

        if (successfulTests != testResults.size()) {
            System.out.print("\033[0;31m [x] ");
        } else {
            System.out.print("\033[0;32m [✓] ");
        }

        System.out.print(successfulTests+"/"+testResults.size());
        System.out.println("\033[0m");

        for (TestResult testResult : testResults) {
            testResult.appendOrigin(name);
        }

        if (level == 0) {
            System.out.println();
            System.out.println("Failed tests:");

            ArrayList<TestResult> failedTests = new ArrayList<>();

            for (TestResult result : testResults) {
                if (!result.isPassed()) {
                    failedTests.add(result);
                }
            }

            for (TestResult failedTest : failedTests) {
                System.out.println(failedTest);
            }
        }

        return testResults;
    }


}
