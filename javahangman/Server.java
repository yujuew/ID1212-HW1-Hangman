/*
Server side:
Contains the main game logic.
Reads the input data from the client.
Senting relpy messages to clients.
Multithread,can handle mutiple users.
 */
package javahangman;

/**
 *
 * @author Shine
 */
import acm.util.RandomGenerator;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.Scanner;

public class Server {

    public static final int PORT = 12345;//the port server listens to     

    public static void main(String[] args) {
        System.out.println("Server Up...\n");
        Server server = new Server();
        server.init();
    }

    public void init() {
        try {
            ServerSocket serverSocket = new ServerSocket(PORT);
            while (true) {
                // connect with client   
                Socket client = serverSocket.accept();
                // handle this thread
                new HandlerThread(client);
            }
        } catch (Exception e) {
            System.out.println("Server Error: " + e.getMessage());
        }
    }

    private class HandlerThread implements Runnable {

        private Socket socket;
        private String sw;
        private String hiddenWord;
        private String guess;
        private int tries;
        private int score = 0;
        DataOutputStream out;

        //start a new thread when a client come
        public HandlerThread(Socket client) {
            socket = client;
            new Thread(this).start();
        }

        public void run() {
            try {
                out = new DataOutputStream(socket.getOutputStream());
                String s = "*******  Welcome to the Hangman Game!  *******\n****Please guess the word or guess a letter fo the word!*****";
                out = new DataOutputStream(socket.getOutputStream());
                s = s + "\n *************** new game **************";
                out.writeUTF(s);

                while (true) {
                    setUpGame();
                }
                //report error
            } catch (Exception e) {
                System.out.println("Server run Error: " + e.getMessage());
            } finally {
                if (socket != null) {
                    try {
                        socket.close();
                    } catch (Exception e) {
                        socket = null;
                        System.out.println("Server finally Error:" + e.getMessage());
                    }
                }
            }
        }

        void setUpGame() throws IOException {
            sw = pickWord();
            tries = guessChances();
            hiddenWord = showNumberOfLetters(sw);

            while (true) {
                String s = "Now you have\"" + tries + "\"guesses left.";
                out.writeUTF(s);//write 1 
                System.out.println("write1");
                //if there are left tries
                if (0 != tries) {
                    System.out.println(sw);//server shows the answer
                    String ss = "Now the word looks like this:" + hiddenWord + "\tYour current score is: " + score;
                    out.writeUTF(ss);//write 2 
                    System.out.println("write2");

                    input();//input word

                    boolean isfinish = check();
                    if (isfinish) {
                        return;
                    }
                } else {
                    score--;
                    String s2 = "You have run out of chances!!\nThe answer word is: " + sw + "\nYour current score is: " + score;
                    out.writeUTF(s2);// write 2 
                    System.out.println("write2");

                    DataInputStream input = new DataInputStream(socket.getInputStream());
                    input.readUTF();//read 

                    String ss2 = "\n************** NEW GAME ****************\n";
                    out.writeUTF(ss2);//write3 
                    System.out.println("write3");
                    System.out.println("Run out of chances!!"); //Display on the server side
                    System.out.println("The answer word is: " + sw); //Display on the server side
                    System.out.println("Current score is: " + score); //Display on the server side                   
                    return;
                }
            }
        }

        //show the hidden word
        private String showNumberOfLetters(String sw) {
            String result = "";
            for (int i = 0; i < sw.length(); i++) {
                result = result + "_";
            }
            return result;
        }

        //get input from client 
        private void input() throws IOException {
            //System.out.println("Please guess the word or guess a letter fo the word!");
            //System.out.println("Now You have " + tries + " guesses left.");
            // Scanner input = new Scanner(System.in);
            DataInputStream input = new DataInputStream(socket.getInputStream());
            guess = input.readUTF();
            System.out.println("Client guess:" + guess);
            System.out.println("read");
        }

        //count the initial chances
        private int guessChances() {
            int chances = sw.length();
            return chances;
        }

        //check the input
        private boolean check() throws IOException {
            //if input is a whole word
            if (guess.length() == sw.length()) {
                for (int i = 0; i < sw.length(); i++) {
                    if (guess.charAt(i) != sw.charAt(i)) {
                        System.out.println("You are wrong! Guess again!");
                        String s3 = "You are wrong! Guess again!";
                        out.writeUTF(s3);//write 3 
                        System.out.println("write3");
                        tries--;
                        return false;
                    }
                    if (i == sw.length() - 1) {
                        System.out.println("Right! Congratulation");
                        String ss3 = " Congratulation!!You've got the correct word!" + "\n\n************** new game ****************\n";
                        out.writeUTF(ss3);//write 3 
                        System.out.println("write3");
                        score++;
                        return true;
                    }
                }
            } //if the input is a letter
            else if (guess.length() == 1) {
                //to update the displayed hiddenWord
                for (int i = 0; i < sw.length(); i++) {
                    if (guess.charAt(0) == sw.charAt(i)) {
                        if (i > 0) {
                            hiddenWord = hiddenWord.substring(0, i) + sw.charAt(i) + hiddenWord.substring(i + 1);
                        }
                        if (i == 0) {
                            hiddenWord = sw.charAt(i) + hiddenWord.substring(1);
                        }
                    }
                }

                //to check if client has already got whole word
                if (hiddenWord.equals(sw)) {
                    System.out.println("Congratulation! You've get the correct word!");
                    String s1 = "Congratulation! You've get the correct word!" + "\n\n************** new game ****************\n";
                    out.writeUTF(s1);
                    System.out.println("write3");
                    score++;
                    return true;
                }

                //check if client input the correct letter
                for (int a = 0; a < sw.length(); a++) {
                    if (guess.charAt(0) == sw.charAt(a)) {
                        String s7 = "Yes!You've got a correct letter!Continue guessing!";
                        out.writeUTF(s7);//wriete3
                        System.out.println("write3");
                        break;
                    } else {
                        if (a == sw.length() - 1) {
                            System.out.println("There is no \"" + guess + "\" in the word!");
                            String s4 = "There is no \"" + guess + "\" in the word!  Guess again!";//
                            out.writeUTF(s4);
                            System.out.println("write3");
                            tries--;
                        }
                    }
                }
            } //if input is neither the whole word or a letter
            else {
                System.out.println("Illigal letters! You can only guess ONE letter or the WHOLE word a time! Try again!");
                String s5 = "Illigal letters! You can only guess ONE letter or the WHOLE word a time! Try again!";//write3
                out.writeUTF(s5);
                System.out.println("write3");
            }
            return false;
        }

        //pick a random word from the file
        private String pickWord() {
            HangmanWord hangmanWord = new HangmanWord();
            RandomGenerator randomgen = new RandomGenerator();
            int randomWord = randomgen.nextInt(0, (hangmanWord.getWordCount() - 1));
            String pickedWord = hangmanWord.getWord(randomWord);
            return pickedWord;
        }

    }

}
