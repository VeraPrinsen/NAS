package testpackage;

import checksum.CyclicRedundancyCheck;
import fileoperators.FileReaderClass;

public class TestCRC {

    public static void main(String[] args) {
        String goodSentence = "Dit is een stuk data. Dit is een stuk data. Dit is een stuk data. Dit is een stuk data. Dit is een stuk data. Dit is een stuk data. ";
        String badSentence = "Dit is een stup data";

        long goodCRC = CyclicRedundancyCheck.checksum(goodSentence.getBytes());
        long badCRC = CyclicRedundancyCheck.checksum(badSentence.getBytes());

        System.out.println(goodCRC);
        System.out.println(badCRC);

//        byte[] sentence = FileReaderClass.fileToByteArray("/Users/vera.prinsen/Documents/Module2/Eindopdracht/testFiles/100MB.zip");
//        long crc = CyclicRedundancyCheck.checksum(sentence);
//        System.out.println(crc);
    }
}
