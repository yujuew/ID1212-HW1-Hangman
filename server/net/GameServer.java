package server.net;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.net.SocketException;

public class GameServer {
    private static final int LINGER_TIME = 5000;
    private static final int TIMEOUT_TEN_MINUTES = 600000;
    private int portNumber = 3333;

    public void start(){
        try {
            ServerSocket listeningSocket = new ServerSocket(portNumber);
            while(true){
                Socket clientSocket = listeningSocket.accept();
                startHandler(clientSocket);
            }
        } catch (IOException ioe) {
            throw new UncheckedIOException(ioe);
        }
    }

    private void startHandler(Socket clientSocket) throws SocketException {
        clientSocket.setSoLinger(true, LINGER_TIME);
        clientSocket.setSoTimeout(TIMEOUT_TEN_MINUTES);
        ClientHandler clientHandler = new ClientHandler(clientSocket);
        Thread handlerThread = new Thread(clientHandler);
        handlerThread.setPriority(Thread.MAX_PRIORITY);
        handlerThread.start();
    }
    
    public static void main(String[] args){
        GameServer server = new GameServer();
        server.start();
    }
    
}