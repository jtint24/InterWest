package Parser;

public class EventLibrary {
    public abstract static class Event {
        String name;

        @Override
        public String toString() {
            return name;
        }
    }
    public static class OpenEvent extends Event {
        TreeKind kind;

        public OpenEvent(TreeKind kind) {

            this.kind = kind;
            name = "open";
        }

        @Override
        public String toString() {
            return name+"("+kind+")";
        }

    }

    public static class  CloseEvent extends Event {
        CloseEvent() {
            name = "close";
        }
    }
    public static class  AdvanceEvent extends Event {
        AdvanceEvent() {
            name = "advance";
        }
    }
}
