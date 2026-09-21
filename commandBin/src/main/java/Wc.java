import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;

/**
 * {@code wc [<file>...]}: prints newline, word, and byte counts for each file, and a total line
 * if more than one file is specified. A word is a non-zero-length sequence of characters
 * delimited by white space.
 */
public class Wc extends ShellCommand {
    /**
     * Format specifier for printing output counts. Do not change.
     */
    public static final String formatSpecifier = "%8d %8d %8d %s%n";

    public Wc(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        ShellCommand.start(Wc.class, args);
    }

    @Override
    protected void runCommand() throws IOException {
        if (cmdArgs.length == 0) {
            int[] counts = countStream(System.in);
            System.out.printf(formatSpecifier, counts[0], counts[1], counts[2], "");
            return;
        }

        int totalLines = 0, totalWords = 0, totalBytes = 0;

        for (String arg : cmdArgs) {
            try {
                Path p = Path.of(arg);
                checkExists(p);

                if (Files.isDirectory(p)) {
                    System.err.printf("%s: %s: Is a directory%n", cmdName, p);
                    System.out.printf(formatSpecifier, 0, 0, 0, arg);
                    continue;
                }

                int[] counts = countStream(new FileInputStream(p.toFile()));
                System.out.printf(formatSpecifier, counts[0], counts[1], counts[2], arg);
                totalLines += counts[0];
                totalWords += counts[1];
                totalBytes += counts[2];
            } catch (IllegalArgumentException e) {
                System.err.println(e.getMessage());
            }
        }

        if (cmdArgs.length > 1) {
            System.out.printf(formatSpecifier, totalLines, totalWords, totalBytes, "total");
        }
    }

    private int[] countStream(InputStream stream) throws IOException {
        int lines = 0, words = 0, bytes = 0;
        String line;
        while ((line = readLineRaw(stream)) != null) {
            lines++;
            bytes += line.getBytes(StandardCharsets.UTF_8).length;
            String trimmed = line.trim();
            if (!trimmed.isEmpty()) {
                words += trimmed.split("\\s+").length;
            }
        }
        return new int[]{lines, words, bytes};
    }
}
