package fileoperators;

import java.io.FileOutputStream;
import java.io.OutputStream;

public class FileWriterClass {

    public static void byteArrayToFile(byte[] bArray, String outputFilename) throws Exception {
        OutputStream out = new FileOutputStream(outputFilename);
        out.write(bArray);
        out.close();
    }
}
