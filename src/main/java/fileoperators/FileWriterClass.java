package fileoperators;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileWriterClass {

    public static void byteArrayToFile(byte[][] bArray, String outputFilename) {
        OutputStream out = null;
        try {
            out = new FileOutputStream(outputFilename);
            for (byte[] dataBytes : bArray) {
                out.write(dataBytes);
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
