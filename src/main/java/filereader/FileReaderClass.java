package filereader;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileReaderClass {


    public static byte[] fileToByteArray(File file) {
        byte[] array = new byte[0];
        try {
            array = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return array;
    }
}
