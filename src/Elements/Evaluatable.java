package Elements;

import Interpreter.StaticReductionContext;
import Interpreter.ValidationContext;
import Utils.Result;

public interface Evaluatable {
    ValidationContext validate(ValidationContext context);
    StaticReductionContext initializeStaticValues(StaticReductionContext context);
}
