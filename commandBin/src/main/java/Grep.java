import java.io.IOException;
import java.nio.file.*;
import java.util.regex.Pattern;

/**
 * {@code grep <pattern> [<file>...]}: prints every line, from the given files or from standard
 * input, that matches the {@link java.util.regex.Pattern regular expression} {@code pattern}.
 *
 * <p>With more than one file, each matching line is prefixed with {@code "<filename>:"}.
 */
public class Grep extends ShellCommand {

    public Grep(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        ShellCommand.start(Grep.class, args);
    }

    @Override
    protected void runCommand() throws IOException {
        if (cmdArgs.length == 0) {
            System.err.printf("Usage: grep <pattern> [<file>...]%n");
            return;
        }

        Pattern pattern = Pattern.compile(cmdArgs[0]);
        boolean multiFile = cmdArgs.length > 2;

        if (cmdArgs.length == 1) {
            String line;
            while ((line = readLineRaw(System.in)) != null) {
                line = line.replaceAll("\\r?\\n$", "");
                if (pattern.matcher(line).find()) {
                    System.out.println(line);
                }
            }
            return;
        }

        for (int i = 1; i < cmdArgs.length; i++) {
            try {
                Path p = Path.of(cmdArgs[i]);
                checkExists(p);
                checkIsNotDir(p);
                for (String line : Files.readAllLines(p)) {
                    if (pattern.matcher(line).find()) {
                        System.out.println(multiFile ? cmdArgs[i] + ":" + line : line);
                    }
                }
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }
    }
}
