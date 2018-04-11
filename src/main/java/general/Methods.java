package general;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class Methods {

    public static byte[] concat(byte[] arrayA, byte[] arrayB) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write(arrayA);
            outputStream.write(arrayB);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte c[] = outputStream.toByteArray( );
        return c;
    }
}
