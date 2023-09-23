package Testing;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

public class TestSuite implements Testable {
    Testable[] subTests;
    String name;

    public TestSuite(String name, Testable... subTests) {
        this.name = name;
        this.subTests = subTests;

        int count = 0;
        for (Testable subTest : subTests) {
            if (subTest instanceof Test) {
                count++;
                ((Test) subTest).name = ""+count;
            }
        }
    }

    public TestSuite(File directory) {
        this.name = directory.getName();

        File[] listOfFiles = directory.listFiles();
        if (listOfFiles == null) {
            return;
        }

        this.subTests = new Testable[listOfFiles.length];
        for (int i = 0; i<listOfFiles.length; i++) {
            try {
                this.subTests[i] = Testable.fromFile(listOfFiles[i]);
            } catch (FileNotFoundException ignored) {}
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

            ArrayList<TestResult> failedTests = new ArrayList<>();



            for (TestResult result : testResults) {
                if (!result.isPassed()) {
                    failedTests.add(result);
                }
            }

            if (failedTests.size() > 0) {
                System.out.println("Failed tests:");
            } else {
                System.out.println("No Failed Tests!");
            }

            for (TestResult failedTest : failedTests) {
                System.out.println(failedTest);
            }
        }

        return testResults;
    }


}
