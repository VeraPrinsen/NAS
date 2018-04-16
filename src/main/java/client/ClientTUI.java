package client;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * The ClientTUI takes care of the in- and output to and from the console.
 *
 * @author vera.prinsen
 */
// TODO: Clean up EXIT code
public class ClientTUI {

    private BufferedReader in;

    public ClientTUI() {
        in = new BufferedReader(new InputStreamReader(System.in));
    }

    /**
     * Prints a message on the console of this particular client.
     */
    public void print(String msg) {
        System.out.println(msg);
    }

    /**
     * Method reads a String from the input console.
     */
    public String readString(String prompt) {
        String msg;
        try {
            System.out.print(prompt + ": ");
            msg = in.readLine();
            return msg;
        } catch (IOException e) {
            // Client console is disconnected:
            // send server that client is not available and will exit.
            // return Protocol.Client.EXIT;
            return "EXIT";
        }
    }

    /**
     * Method reads an integer from the input console.
     * (using readString(String prompt))
     */
    public int readInt(String prompt) {
        boolean inputOK = false;
        int input = 0;

        while (!inputOK) {
            try {
                String inputString = readString(prompt);
                input = Integer.parseInt(inputString);
                inputOK = true;
            } catch (NumberFormatException e) {
                print("Input must be an integer.");
            }
        }

        return input;
    }
}
