import tools.jackson.core.JacksonException;
import tools.jackson.databind.ObjectMapper;
import model.MemorisationSpec;

import java.io.File;

public class InputReader {
    public static MemorisationSpec read(String filename) {
        ObjectMapper mapper = new ObjectMapper();

        File file = new File(filename);

        try {
            return mapper.readValue(file, MemorisationSpec.class);
        } catch (JacksonException e) {
            throw new RuntimeException(e);
        }
    }
}
