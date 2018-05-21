package server.word;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.Random;
import java.util.Scanner;

public class WordList {
    Scanner s;
    ArrayList<String> words = new ArrayList<>();
    Random random = new Random();
    private static WordList instance;

    private WordList() throws FileNotFoundException {
         s = new Scanner(new File("words.txt"));
         while(s.hasNextLine()){
             words.add(s.nextLine());
         }
         s.close();
    }

    public static WordList getInstance() throws FileNotFoundException {
        if(instance == null){
            instance = new WordList();
        }
        return instance;
    }

    public String getWord(){
        int index = random.nextInt(words.size());
        return words.get(index);
    }
}
