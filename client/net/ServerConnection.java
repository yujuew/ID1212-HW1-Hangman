package client.net;

import com.google.gson.Gson;
import shared.Message;
import shared.MessageType;
import java.io.*;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ServerConnection {
    private static final int TIMEOUT_HALF_MINUTE = 30000;
    private static final int TIMEOUT_TEN_MINUTES = 600000;
    private Socket socket;
    private PrintWriter toServer;
    private BufferedReader fromServer;
    private volatile boolean connected;
    private Gson g = new Gson();

    public void connect(String host, int port, ServerMessageHandler serverMessageHandler) throws IOException {
        socket = new Socket();
        socket.connect(new InetSocketAddress(host,port),TIMEOUT_HALF_MINUTE);
        socket.setSoTimeout(TIMEOUT_TEN_MINUTES);
        connected = true;
        boolean autoflush = true;
        toServer = new PrintWriter(socket.getOutputStream(),autoflush);
        fromServer = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        new Thread(new Listener(serverMessageHandler)).start();
    }

    public void disconnect() throws IOException {
        Message msg = new Message(MessageType.QUIT);
        toServer.println(g.toJson(msg));
        socket.close();
        socket = null;
        toServer = null;
        connected = false;
    }

    public void startGame(){
        Message msg = new Message(MessageType.START);
        toServer.println(g.toJson(msg));
    }

    public void makeGuess(String guess){
        Message msg = new Message(MessageType.GUESS, guess);
        toServer.println(g.toJson(msg));
    }

    private class Listener implements Runnable{
        private final ServerMessageHandler serverMessageHandler;

        private Listener(ServerMessageHandler serverMessageHandler){
            this.serverMessageHandler = serverMessageHandler;
        }

        public void run() {
            try {
                while(true){
                    Message msg = g.fromJson(fromServer.readLine(), Message.class);
                    if(msg != null){
                        serverMessageHandler.handleMsg(msg);
                    }
                }
            } catch (IOException e) {
                if(connected){
                    e.printStackTrace();
                }
            }
        }
    }
}
