package general;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.ByteBuffer;

public class Utils {

    /**
     * Concatenates a variable number of byte arrays to each other
     */
    public static byte[] byteConcat(byte[]... args) {
        byte[] arrayA = args[0];

        for (int i = 1; i < args.length; i++) {
            byte[] arrayB = args[i];

            arrayA = concat2byte(arrayA, arrayB);
        }

        return arrayA;
    }

    /**
     * Concatenates 2 byte arrays to each other
     */
    public static byte[] concat2byte(byte[] arrayA, byte[] arrayB) {
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream( );
        try {
            outputStream.write(arrayA);
            outputStream.write(arrayB);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return outputStream.toByteArray( );
    }

    /**
     * converts a byte array into an integer value
     */
    public static int byteArrayToInt(byte[] byteArray) {
        int tempInt = 0;
        for (int i = 0; i < byteArray.length; i++) {
            tempInt = tempInt | ((byteArray[i] & 0xFF) << (8*(byteArray.length-i-1)));
        }
        return tempInt;
    }

    /**
     * converts a integer into a byte array of nBytes
     * if the integer cannot fit in that many bytes, null is returned.
     */
    public static byte[] intToByteArray(int value, int nBytes) {
        if (Math.pow(2,nBytes*8) > value-1) {
            byte[] tempByte = new byte[nBytes];
            for (int i = 0; i < nBytes; i++) {
                tempByte[i] = (byte) ((value >> 8*(nBytes-i-1)) & 0xFF);
            }
            return tempByte;
        } else {
            // value to large for amount of bytes
            return null;
        }
    }
}
