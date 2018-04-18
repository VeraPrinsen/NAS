package fileoperators;

import general.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

/**
 * Reads a file into a byte array.
 */
public class FileReaderClass {

    public static byte[] fileToByteArray(String filename) {
        File file = new File(filename);
        byte[] bArray = new byte[(int) file.length()];
        try {
            FileInputStream fileInputStream = new FileInputStream(file);
            fileInputStream.read(bArray);
            fileInputStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bArray;
    }
}
