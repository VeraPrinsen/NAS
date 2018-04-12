package client;

public class TextResources {

    public static class General {

        public static void welcomeMessage() {
            prt("");
            prt("             (");
            prt("              )");
            prt("         __..---..__");
            prt("     ,-='  /  |  \\  `=-.");
            prt("    :--..___________..--;");
            prt("     \\.,_____________,./");
            prt("");
            prt("");
            prt("  Welcome to the ApplePi Unit!");
            prt("");
        }

        public static void mainMenu() {
            prt("");
            prt("What do you want to do?:");
            prt("Download a file .................. 1");
            prt("Upload a file .................... 2");
            prt("List of downloadable files ....... 3");
            prt("Send testpacket (small) .......... 4");
            prt("Send testpacket (large) .......... 5");
            prt("");
            prt("Exit ............................. 0");
            prt("");
        }

        public static void exit() {
            prt("");
            prt("Goodbye!");
        }

    }

    public static class Download {

        public static void askForFile() {
            //
        }
    }

    private static void prt(String msg) {
        System.out.println(msg);
    }
}
