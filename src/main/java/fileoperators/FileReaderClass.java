package fileoperators;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class FileReaderClass {

    public static byte[] fileToByteArray(String filename) {
        byte[] bArray = new byte[0];
        File file = new File(filename);
        try {
            bArray = Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bArray;
    }
}
