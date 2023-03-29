package Parser;

public abstract class ParseDecision {


    public static class Accept extends ParseDecision {
        Parser.ParsingState pushState;
    }

    public static class Reduce extends ParseDecision {

    }

    public static class Error extends ParseDecision {

    }

    public static class Conflict extends ParseDecision {

    }

    public class Shift extends ParseDecision {
        Parser.ParsingState pushState;
    }
}
