import input.Input;
import input.InputReader;
import output.Day;
import output.Output;
import output.OutputWriter;

import java.util.List;

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

        Output dummyOutput = new Output(List.of(
                new Day("Matthew 1-4", 14),
                new Day("Matthew 5-7", 57)
        ));

        OutputWriter.write(dummyOutput, outputFilename);
    }
}
