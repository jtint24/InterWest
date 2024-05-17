package Main;

import Interpreter.InterpretationSession;
import Testing.TestSuite;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.*;

public class Main {
    public static void main(String[] args) throws IOException {
        //args = new String[]{"run", "-f", "/Users/joshuatint/Desktop/code/test-lang/src/interwest1.west"};
        // args = new String[]{"help"};
        args = new String[]{"validate", "interwest1.west"};
        if (args.length == 0) {
            argParseError("Please select at least one of the following commands: help, run, validate, test");
        }
        switch (args[0]) {
            case "help" -> printHelp();
            case "run" -> runFile(args);
            case "test" -> runTests(args);
            case "validate" -> validateFile(args);
        }
    }

    public static void runTests(String[] args) {
        String[] possibleFlags = {"-d"};
        String[] requiredFlags = {"-d"};

        String[] flagArgs = new String[args.length-1];

        System.arraycopy(args, 1, flagArgs, 0, args.length - 1);
        HashMap<String, String> flags = getFlags(flagArgs, possibleFlags);

        validateFlags(flags, List.of(possibleFlags), List.of(requiredFlags));

        TestSuite testSuite = new TestSuite(new File(flags.get("-f")));

        testSuite.getResults();
    }
    public static void validateFile(String[] args) throws IOException {
        String[] possibleFlags = {"-f"};
        String[] requiredFlags = {"-f"};

        String[] flagArgs = new String[args.length-1];
        System.arraycopy(args, 1, flagArgs, 0, args.length - 1);


        HashMap<String, String> flags = getFlags(flagArgs, possibleFlags);

//        for (Map.Entry<String, String> entry : flags.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }

        validateFlags(flags, List.of(possibleFlags), List.of(requiredFlags));

        String fileName = flags.get("-f");

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String program = sb.toString();

            InterpretationSession sesh = new InterpretationSession(program);
            sesh.validateAST();
        }
    }

    public static void printHelp() {
        // TODO
        System.out.println("some help message");
    }

    public static void validateFlags(HashMap<String, String> flags, Collection<String> validFlags, Collection<String> requiredFlags) {
        HashSet<String> unfulfilledFlags = new HashSet<>();
        for (String requiredFlag : requiredFlags) {
            if (!flags.containsKey(requiredFlag)) {
                unfulfilledFlags.add(requiredFlag);
            }
        }

        Set<String> invalidFlags = new HashSet<>();
        for (String givenFlag : flags.keySet()) {
            if (!validFlags.contains(givenFlag)) {
                invalidFlags.add(givenFlag);
            }
        }

        String errorMessage = "";
        if (invalidFlags.size() > 0) {
            errorMessage = "I don't recognize the following flags: "+String.join(", ", invalidFlags) + "\n"
                    + "The valid flags are: "+String.join(", ", validFlags);
            if (unfulfilledFlags.size() > 0) {
                errorMessage += "\n";
            }
        }
        if (unfulfilledFlags.size() > 0) {
            errorMessage += "I'm missing the required flags: " + String.join(", ", unfulfilledFlags);
        }

        if (!errorMessage.equals("")) {
            argParseError(errorMessage);
        }
    }

    public static void runFile(String[] args) throws IOException {
        String[] possibleFlags = {"-f"};
        String[] requiredFlags = {"-f"};

        String[] flagArgs = new String[args.length-1];
        System.arraycopy(args, 1, flagArgs, 0, args.length - 1);


        HashMap<String, String> flags = getFlags(flagArgs, possibleFlags);

//        for (Map.Entry<String, String> entry : flags.entrySet()) {
//            System.out.println(entry.getKey() + " " + entry.getValue());
//        }

        validateFlags(flags, List.of(possibleFlags), List.of(requiredFlags));

        String fileName = flags.get("-f");

        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            StringBuilder sb = new StringBuilder();
            String line = br.readLine();

            while (line != null) {
                sb.append(line);
                sb.append(System.lineSeparator());
                line = br.readLine();
            }
            String program = sb.toString();

            InterpretationSession sesh = new InterpretationSession(program);
            sesh.runSession();
        }
    }

    public static void argParseError(String message) {
        System.out.println(message);
        System.exit(1);
    }

    public static HashMap<String, String> getFlags(String[] flagArgs, String[] unassignedArgs) {
        HashMap<String, String> argMap = new HashMap<>();

        int unnassignedArgCount = 0;
        String flag = "";

        for (String arg : flagArgs) {
            if (arg.startsWith("-")) {
                flag = arg;
                continue;
            } else {
                if (flag.equals("")) {
                    if (unnassignedArgCount < unassignedArgs.length) {
                        flag = unassignedArgs[unnassignedArgCount];
                        unnassignedArgCount++;
                    } else {
                        argParseError("I don't know what `"+arg+"` means. It's an unlabeled argument, but there are too many unlabeled arguments for the pattern I need.");

                    }
                }
            }
            if (!argMap.containsKey(flag)) {
                argMap.put(flag, arg);
                flag = "";
            } else {
                argParseError("There are two conflicting arguments for the `"+flag+"` flag. The arguments are `"+argMap.get(flag)+"`, and `"+arg+"`.");
            }
        }
        return argMap;
    }
}
