package testpackage;

import general.Utils;

public class TestEmptyByte {

    public static void main(String[] args) {
        String sentence = "Dit is data";

        byte[] data1 = sentence.getBytes();
        byte[] data2 = new byte[0];

        byte[] dataTotal = Utils.concat2byte(data1, data2);

        System.out.println(new String(dataTotal));
    }
}
