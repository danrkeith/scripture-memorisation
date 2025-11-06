package input;

import tools.jackson.databind.ObjectMapper;

import java.io.File;

public class InputReader {
    public static Input read(String filename) {
        ObjectMapper mapper = new ObjectMapper();

        File file = new File(filename);

        return mapper.readValue(file, Input.class);
    }
}
