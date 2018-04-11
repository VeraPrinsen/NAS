package filereader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileReaderClass {

    public static byte[] fileToByteArray(String filename) {
        byte[] array = new byte[0];
        File file = new File(filename);
        try {
            array = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }
}
