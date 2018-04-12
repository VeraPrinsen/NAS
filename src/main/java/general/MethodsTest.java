package general;

import org.junit.*;

public class MethodsTest {

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

        byte[] result = Methods.byteConcat(sentenceA.getBytes(), sentenceB.getBytes(), sentenceC.getBytes(), sentenceD.getBytes());

        Assert.assertArrayEquals(totalSentence.getBytes(), result);
    }

    @Test
    public void concat2byte() {
        String sentenceA = "Dit is ";
        String sentenceB = "een test.";
        String totalSentence = sentenceA + sentenceB;

        byte[] result = Methods.concat2byte(sentenceA.getBytes(), sentenceB.getBytes());

        Assert.assertArrayEquals(totalSentence.getBytes(), result);
    }

    @Test
    public void byteArrayToInt() {
        byte[] byteArray = new byte[2];
        byteArray[0] = 2;
        byteArray[1]= 2;

        int result = Methods.byteArrayToInt(byteArray);

        Assert.assertEquals(514, result);
    }

    @Test
    public void intToByteArray() {
        int a = 10;
        byte[] b = Methods.intToByteArray(a, 1);
        byte[] aByte = new byte[1];
        aByte[0] = 0b0001010;

        Assert.assertArrayEquals(aByte, b);
    }
}