package server.net;

import com.google.gson.Gson;
import shared.Message;
import shared.MessageType;
import shared.ThreadSafeStdOut;
import server.word.WordList;
import java.io.*;
import java.net.Socket;

public class ClientHandler implements Runnable{
    private final Socket clientSocket;
    private final ThreadSafeStdOut consoleOut = new ThreadSafeStdOut();
    private BufferedReader fromClient;
    private PrintWriter toClient;  
    private WordList wl;
    private Gson g = new Gson();
    private StringBuilder correctWord;
    private StringBuilder currentWord;
    private final String clientIdentifier;
    private int score = 0;
    private int remainingAttempts;
    private int wordLength;
    private boolean ifConnected;
    private boolean ifRunning = false;

    public ClientHandler(Socket clientSocket){
        this.clientSocket = clientSocket;
        clientIdentifier = clientSocket.getInetAddress().getHostAddress() + ":" + clientSocket.getPort();
        ifConnected = true;
    }

    public void run() {
        boolean autoFlush = true;
        try {
            fromClient = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            toClient = new PrintWriter(clientSocket.getOutputStream(), autoFlush);
            consoleOut.println("New client connected from: " + clientIdentifier);
            Message msg = new Message(MessageType.INFO, "Type START to start a new game; Type QUIT to disconnect.");
            toClient.println(g.toJson(msg));
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }

        while(ifConnected){
            try {
                Message msg = g.fromJson(fromClient.readLine(), Message.class);
                if(msg.getMsgType() == null){
                    Message m = new Message(MessageType.INFO, "Invalid message!!");
                    toClient.println(g.toJson(m));
                }
                else{
                    switch(msg.getMsgType()){
                        case QUIT:
                            disconnectClient();
                            break;
                        case START:
                            startNewGame();
                            break;
                        case GUESS:
                            if(ifRunning){
                                makeGuess(msg.getMessage());
                            }
                            else{
                                Message m = new Message(MessageType.INFO, "Type Start to start a new game!");
                                toClient.println(g.toJson(m));
                            }
                            break;
                        default :
                            Message m = new Message(MessageType.INFO, "Unknown command!");
                            toClient.println(g.toJson(m));
                            consoleOut.println("Received unknown command from: " + clientIdentifier);
                            break;
                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

    private void startNewGame(){
        consoleOut.println("Starting a new game for client " + clientIdentifier);
        try {
            ifRunning = true;
            wl = WordList.getInstance();
            correctWord = new StringBuilder(wl.getWord().toUpperCase());
            currentWord = new StringBuilder();
            wordLength = correctWord.length();
            //print out current hidden word
            for(int i = 0; i < wordLength; i++){
                currentWord.append("_ ");
            }
            remainingAttempts = wordLength;
            Message m = new Message(MessageType.GAMEINFO,currentWord.toString(),remainingAttempts,score,ifRunning);
            toClient.println(g.toJson(m));
            consoleOut.println(clientIdentifier + " : " + correctWord.toString());
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

    private void makeGuess(String guessString){
        String guess = guessString.toUpperCase();
        //correctly guess the whole word
        if((guess.length() > 1) && correctWord.toString().equals(guess)){
            score++;
            ifRunning = false;
            for(int i = 0; i < wordLength; i++){
                currentWord.setCharAt(2*i,correctWord.charAt(i));
            }
            Message m = new Message(MessageType.GAMEINFO,currentWord.toString(),remainingAttempts,score,ifRunning);
            toClient.println(g.toJson(m));
        }
        //guess 1 letter
        else if(guess.length() == 1){
            if(correctWord.toString().contains(guess)){
                char c = guess.charAt(0);
                for(int i = 0; i < wordLength; i++){
                    if(correctWord.charAt(i) == c)
                        currentWord.setCharAt(i*2,c);
                }
                Message m;
                if(verifyWord(currentWord)){
                    score++;
                    ifRunning = false;
                    m = new Message(MessageType.GAMEINFO,currentWord.toString(),remainingAttempts,score,ifRunning);
                }
                else{
                    m = new Message(MessageType.GAMEINFO,currentWord.toString(),remainingAttempts,score,ifRunning);
                }
                toClient.println(g.toJson(m));
            }
            else{
                remainingAttempts--;
                //run out attempts
                if(remainingAttempts == 0){
                    ifRunning = false;
                    score--;
                    Message m = new Message(MessageType.GAMEINFO,currentWord.toString(),remainingAttempts,score,ifRunning);
                    toClient.println(g.toJson(m));
                }
                else{
                    Message m = new Message(MessageType.GAMEINFO,currentWord.toString(),remainingAttempts,score,ifRunning);
                    toClient.println(g.toJson(m));
                }
            }

        }
    }

    private boolean verifyWord(StringBuilder guess){
        StringBuilder str = new StringBuilder();
        for(int i = 0; i < guess.length(); i++){
            if(guess.charAt(i) != ' '){
                str.append(guess.charAt(i));
            }
        }
        return str.toString().equals(correctWord.toString());
    }

    private void disconnectClient(){
        try {
            clientSocket.close();
            consoleOut.println("Disconnecting client " + clientIdentifier);
        } catch (IOException e) {
            e.printStackTrace();
        }
        ifConnected = false;
    }
}
