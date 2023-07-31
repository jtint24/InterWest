package Testing;

import java.io.FileNotFoundException;
import java.util.List;
import java.io.File;


public interface Testable {
    List<TestResult> getResults();
    List<TestResult> getResults(int level);

    static Testable fromFile(File file) throws FileNotFoundException {
        if (file.isDirectory()) {
            return new TestSuite(file);
        } else {
            return new Test(file);
        }
    }
}
