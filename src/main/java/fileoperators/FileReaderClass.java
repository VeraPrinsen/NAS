package fileoperators;

import general.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;

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
//        byte[] bArray = new byte[0];
//        File file = new File(filename);
//        try {
//            bArray = Files.readAllBytes(file.toPath());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return bArray;
    }
}
