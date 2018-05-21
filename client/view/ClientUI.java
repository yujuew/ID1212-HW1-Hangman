package client.view;

import client.controller.Controller;
import client.net.ServerMessageHandler;
import shared.Message;
import shared.MessageType;
import shared.ThreadSafeStdOut;

import java.util.Scanner;

public class ClientUI implements Runnable {
    private final Scanner input = new Scanner(System.in);
    private static final String PROMT = "> ";
    private Controller controller;
    private boolean running = false;
    private final ThreadSafeStdOut consoleOut = new ThreadSafeStdOut();
    private boolean connected = false;
    private boolean gameRunning = false;

    public void start(){
        controller = new Controller();
        running = true;
        new Thread(this).start();
    }

    public void run() {
        consoleOut.println("To connect to a server, type: connect <host> <port>");
        consoleOut.println("Type Quit to end the program");

        while(running){
            consoleOut.print(PROMT);
            try{
                CmdLine cmd = new CmdLine(input.nextLine());
                switch(cmd.getCmd()){
                    case CONNECT:
                        if(connected){
                            consoleOut.println("You are already connected to the server!");
                        }
                        else{
                            consoleOut.println("Connecting to server...");
                            String host = cmd.getArgs()[0];
                            int port = Integer.valueOf(cmd.getArgs()[1]);
                            controller.connect(host,port,new ServerMessageOutput());}
                        break;
                    case QUIT:
                        if(connected){
                            controller.disconnect();
                            connected = false;
                            consoleOut.println("Disconnected from server");
                            consoleOut.println("To connect to a server, type: connect <host> <port>");
                            consoleOut.println("Type Quit to end");
                        }
                        else{
                            running = false;
                        }
                        break;
                    case START:
                        if(connected){
                            controller.startGame();
                        }
                        else{
                            consoleOut.println("Connect to a server first!");
                            consoleOut.println("To connect to a server, type: connect <host> <port>");
                        }
                        break;
                    case GUESS:
                        if(connected){
                            if(gameRunning){
                                String guess = cmd.getArgs()[0];
                                controller.makeGuess(guess);
                            }
                            else{
                                consoleOut.println("Type Start to begin a new game!");
                            }
                        }
                        else{
                            consoleOut.println("Connect to a server first!");
                            consoleOut.println("To connect to a server, type: connect <host> <port>");
                        }
                        break;
                }
            }
            catch(Exception e){
                e.printStackTrace();
            }
        }
    }

    private class ServerMessageOutput implements ServerMessageHandler {
        @Override
        public void handleMsg(Message msg) {
            if(msg.getMsgType() == MessageType.INFO){
                if(msg.isConnectedToServer()){
                    connected = true;
                }
                consoleOut.println(msg.getMessage());
                consoleOut.print(PROMT);
            }
            else if(msg.getMsgType() == MessageType.GAMEINFO){
                gameRunning = msg.isGameRunning();
                consoleOut.print(msg.getMessage());
                consoleOut.print("\t");
                consoleOut.print("Remaining attempts: " + msg.getRemainingAttempts());
                consoleOut.println(" Score: " + msg.getScore());
                if(!msg.isGameRunning()){
                    consoleOut.println("************Game Ended************\n Type START for another game; Type QUIT to end");
                }
                consoleOut.print(PROMT);
            }
        }
    }
}
