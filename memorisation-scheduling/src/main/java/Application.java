import input.Input;
import input.InputReader;

public class Application {
    static void main(String[] args) {
        if (args.length != 2) {
            System.err.println("Usage: java -jar memorisation-scheduling <input.json> <output.json>");
            System.exit(1);
        }

        String inputFilename = args[0];
        String outputFilename = args[1];

        Input input = InputReader.read(inputFilename);

        System.out.println(input);
    }
}
