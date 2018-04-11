package testpackage;

public class TestByteIntString {

    public static void main(String[] args) {
        int a = 256;
        byte b = (byte) a;
        int c = (int) b & 0xFF;

        System.out.println(a);
        System.out.println(b & 0xFF);
        System.out.println(c);
    }
}
