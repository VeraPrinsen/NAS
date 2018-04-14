package tests;

import general.Utils;
import org.junit.*;

public class UtilsTest {

    @Before
    public void setUp() throws Exception {

    }

    @Test
    public void byteConcat() {
        String sentenceA = "Dit is ";
        String sentenceB = "een test";
        String sentenceC = " voor de multiple ";
        String sentenceD = "byte concat method.";
        String totalSentence = sentenceA + sentenceB + sentenceC + sentenceD;

        byte[] result = Utils.byteConcat(sentenceA.getBytes(), sentenceB.getBytes(), sentenceC.getBytes(), sentenceD.getBytes());

        Assert.assertArrayEquals(totalSentence.getBytes(), result);
    }

    @Test
    public void concat2byte() {
        String sentenceA = "Dit is ";
        String sentenceB = "een test.";
        String totalSentence = sentenceA + sentenceB;

        byte[] result = Utils.concat2byte(sentenceA.getBytes(), sentenceB.getBytes());

        Assert.assertArrayEquals(totalSentence.getBytes(), result);
    }

    @Test
    public void byteArrayToInt() {
        byte[] byteArray = new byte[2];
        byteArray[0] = 2;
        byteArray[1]= 2;

        int result = Utils.byteArrayToInt(byteArray);

        Assert.assertEquals(514, result);
    }

    @Test
    public void intToByteArray() {
        int a = 10;
        byte[] b = Utils.intToByteArray(a, 1);
        byte[] aByte = new byte[1];
        aByte[0] = 0b00001010;

        Assert.assertArrayEquals(aByte, b);


        int c = 266;
        byte[] d = Utils.intToByteArray(c, 2);
        byte[] cByte = new byte[2];
        cByte[1] = 0b00001010;
        cByte[0] = 0b00000001;

        Assert.assertArrayEquals(cByte, d);

    }
}