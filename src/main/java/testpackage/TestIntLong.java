package testpackage;

import checksum.CyclicRedundancyCheck;

public class TestIntLong {

    public static void main(String[] args) {
        byte[] sentence = "Dit is data".getBytes();

        long checksum = CyclicRedundancyCheck.checksum(sentence);
        System.out.println(checksum + " / " + (int) checksum);
    }
}
