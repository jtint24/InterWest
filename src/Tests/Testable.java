package Tests;

import java.util.List;

public interface Testable {
    List<TestResult> getResults();
    List<TestResult> getResults(int level);
}
