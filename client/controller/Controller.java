package client.controller;

import client.net.ServerMessageHandler;
import client.net.ServerConnection;
import shared.Message;
import shared.MessageType;
import java.io.IOException;
import java.io.UncheckedIOException;
import java.util.concurrent.CompletableFuture;

public class Controller {
    private final ServerConnection con = new ServerConnection();
    public void disconnect() throws IOException {
        con.disconnect();
    }

    public void connect(String host, int port, ServerMessageHandler serverMessageHandler){
        CompletableFuture.runAsync(() ->{
            try{
                con.connect(host, port, serverMessageHandler);
            }
            catch(IOException ioe){
                throw new UncheckedIOException(ioe);
            }
        }).thenRun(() -> serverMessageHandler.handleMsg(new Message(MessageType.INFO, "Connected to " + host + " on port " + port,true)));
    }

    public void startGame(){
        CompletableFuture.runAsync(new Runnable() {
            @Override
            public void run() {
                try{
                    con.startGame();
                }
                catch(Exception e){
                }
            }
        });
    }

    public void makeGuess(String guess){
        CompletableFuture.runAsync(() ->{
                con.makeGuess(guess);
        });
    }
}
