package client;

import fileoperators.FileReaderClass;

import javax.swing.*;
import java.awt.*;
import java.io.File;

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
            prt("");
            prt("Exit ............................. 0");
            prt("");
        }

        public static void exit() {
            prt("");
            prt("Goodbye!");
        }

    }

    private static void prt(String msg) {
        System.out.println(msg);
    }
}
