package general;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;

public class Methods {

    public static byte[] concat(byte[] arrayA, byte[] arrayB) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write(arrayA);
            outputStream.write(arrayB);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray( );
    }

    public static int byteArrayToInt(byte[] byteArray) {
        return ByteBuffer.wrap(byteArray).getInt();
    }

    public static byte[] intToByteArray(int value, int nBytes) {
        return ByteBuffer.allocate(nBytes).putInt(value).array();
    }
}
