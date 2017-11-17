/*
This is the stand-alone version of the hangman game.
The structure and logic is first designed here and reused in the Server-Clients version 

NOTE:if plays the Server-Clients version game,this file does not need run.
 */
package javahangman;

import acm.util.RandomGenerator;
import java.util.Scanner;
//import java.util.*;
//import java.io.*;

/**
 *
 * @author Shine
 */
public class JavaHangman {

    private RandomGenerator randomgen = new RandomGenerator();
    private HangmanWord hangmanWord;
    private String hiddenWord;
    private String guess;
    private int tries;
    private int score = 0;
    String sw;

    //pick a random word from the list
    private String pickWord() {
        hangmanWord = new HangmanWord();
        int randomWord = randomgen.nextInt(0, (hangmanWord.getWordCount() - 1));
        String pickedWord = hangmanWord.getWord(randomWord);
        return pickedWord;
    }

    //the main game round
    private int setUpGame() {
        sw = pickWord();
        tries = guessChances();
        hiddenWord = showNumberOfLetters();
        while (true) {
            //to check if any trying chances left.
            if (0 != tries) {
                System.out.println("Now the word looks like this:" + hiddenWord);
                System.out.println("Your current score is: " + score);
                //Check if player get the whole correct word
                if (hiddenWord.equals(sw)) {
                    System.out.println("Congratulation! You've get the correct word\n **********new game**********!");
                    score++;
                    return 0;

                }
                input();
                check();
            } else {
                score--;
                System.out.println("You have run out of chances!!");
                System.out.println("The answer word is: " + sw);
                System.out.println("Your current score is: " + score);
                //gameover()
                break;
            }
        }
        return 0;
    }
    
    //show the hidden word
    private String showNumberOfLetters() {
        String result = "";
        for (int i = 0; i < sw.length(); i++) {
            result = result + "_";
        }
        return result;
    }

    //  input the guess from the keyboard 
    private void input() {
        System.out.println("Please guess the word or guess a letter fo the word!");
        System.out.println("Now You have " + tries + " guesses left.");
        Scanner input = new Scanner(System.in);
        guess = input.next();
        System.out.println("You guess:" + guess);
    }

    // count the initial chances
    private int guessChances() {
        int chances = sw.length();
        return chances;
    }

    //after input, check if the word or the letters client is correct
    private void check() {
        //if guess is a word
        if (guess.length() == sw.length()) {
            for (int i = 0; i < sw.length(); i++) {
                if (guess.charAt(i) == sw.charAt(i)) {
                } else {
                    System.out.println("You are wrong! Guess again!");
                    tries--;
                    break;
                }
            }
            score++;
        } //if guess is a letter
        else if (guess.length() == 1) {
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
            int a;
            for (a = 0; a < sw.length(); a++) {
                if (guess.charAt(0) == sw.charAt(a)) {
                    return;
                }
                if (a == sw.length() - 1) {
                    System.out.println("There is no \"" + guess + "\" in the word!  Guess again!");
                    tries--;
                }
            }
        } //if guess is neither a word or a letter
        else {
            System.out.println("Illigal letters! You can only guess ONE letter or the WHOLE word a time! Try again!");
        }
    }

    public static void main(String[] args) {

        JavaHangman hangman = new JavaHangman();

        System.out.println("*******  Welcome to the Hangman Game!  *******");
        int i = 0;
        while (i == 0) {
            System.out.println("*************** new game **************");
            hangman.setUpGame();
        }
    }

}
