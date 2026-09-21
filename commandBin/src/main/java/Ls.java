import java.io.File;
import java.util.*;

/**
 * {@code ls [<path>...]}: lists files and directories. With no arguments, lists the current
 * directory.
 *
 * <p>Files print first (sorted by name), then directories (sorted by name), blank-line separated.
 * A directory's contents get a {@code "<dir>:"} header only when more than one argument was given.
 */
public class Ls extends ShellCommand {

    public Ls(String[] args) {
        super(args);
    }

    public static void main(String[] args) {
        ShellCommand.start(Ls.class, args);
    }

    @Override
    protected void runCommand() {
        if(cmdArgs.length < 2) {
            printContent(cmdArgs.length == 0 ? "./" : cmdArgs[0]);

        }else {
            File argf = null;

            List<String> fileArgs = new ArrayList<>();
            List<String> dirArgs = new ArrayList<>();

            for(String arg: cmdArgs){
                argf = new File(arg);
                if(!argf.exists()) {
                    System.err.printf("ls: %s: No such file or directory%n", arg);
                }else if(argf.isFile()) {
                    fileArgs.add(arg);
                }else {
                    dirArgs.add(arg);
                }
            }

            Collections.sort(fileArgs);
            Collections.sort(dirArgs);

            for (String fileArg : fileArgs) {
                printContent(fileArg);
            }


            for(int i = 0; i<dirArgs.size(); i++) {
                if(fileArgs.size() > 0 || i > 0) {
                    System.out.println();

                }
                if(dirArgs.size() > 1) {
                    System.out.println(dirArgs.get(i) + ":");
                }

                printContent(dirArgs.get(i));
            }

        }


    }

    private void printContent(String pathname) {
        File f = new File(pathname);

        if(f.isFile()){
            System.out.println(pathname);
            return;
        }

//        Two+ arguments after ls
        File[] contents = f.listFiles();

        if (contents != null) {
            Arrays.sort(contents);

            for(File content:contents){
                System.out.println(content.getName());
            }

        }

    }


}
