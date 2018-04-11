package testpackage;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

public class TestByteBitString {

    public static void main(String[] args) {
        String stringA = "Hallo ik ben een test ";
        byte[] byteA = stringA.getBytes();
        byte byteB = 0b1001000;
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write(byteA);
            outputStream.write(byteB);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte byteC[] = outputStream.toByteArray( );

        String stringA2 = new String(byteC);

        System.out.println(stringA2);
    }
}
