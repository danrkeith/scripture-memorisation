package output;

import tools.jackson.databind.ObjectMapper;

import java.io.File;

public class OutputWriter {
    public static void write(Output output, String filename) {
        ObjectMapper mapper = new ObjectMapper();

        File file = new File(filename);

        mapper.writerWithDefaultPrettyPrinter()
                .writeValue(file, output);
    }
}
