package tests;

import checksum.CyclicRedundancyCheck;
import org.junit.*;

public class CRCTest {

    CyclicRedundancyCheck crc;

    @Before
    public void setUp() throws Exception {
        CyclicRedundancyCheck crc = new CyclicRedundancyCheck();
    }

    @Test
    public void test() {
        String message1 = "Dit is een stuk data";
        String message2 = "Dit is andere data";

        byte[] byteArray1 = message1.getBytes();
        byte[] byteArray2 = message2.getBytes();

        Assert.assertEquals(crc.checksum(byteArray1), crc.checksum(byteArray1));
        Assert.assertNotEquals(crc.checksum(byteArray1), crc.checksum(byteArray2));
    }
    
}