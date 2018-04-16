package checksum;

import java.util.zip.CRC32;

// TODO: CRC DOESNT WORK FOR PACKETS OF THE MAX SIZE
public class CyclicRedundancyCheck {

    public static int checksum(byte[] data) {
        CRC32 crc = new CRC32();
        crc.update(data);
        return (int) crc.getValue();
        //return 0;
    }
}