package shared;

public class Message{
    private MessageType msgType;
    private String message;
    private int remainingAttempts;
    private int score;
    private boolean gameRunning;
    private boolean connectedToServer;

    public Message(MessageType msgType) {
        this.msgType = msgType;
    }

    public Message(MessageType msgType, String message) {
        this.msgType = msgType;
        this.message = message;
    }

    public Message(MessageType msgType, String message, boolean connectedToServer) {
        this.msgType = msgType;
        this.message = message;
        this.connectedToServer = connectedToServer;
    }

    public Message(MessageType msgType, String message, int remainingAttempts, int score, boolean gameRunning) {
        this.msgType = msgType;
        this.message = message;
        this.remainingAttempts = remainingAttempts;
        this.score = score;
        this.gameRunning = gameRunning;
    }

    public MessageType getMsgType() {
        return msgType;
    }

    public String getMessage() {
        return message;
    }

    public int getRemainingAttempts() {
        return remainingAttempts;
    }

    public int getScore() {
        return score;
    }

    public boolean isGameRunning() {
        return gameRunning;
    }

    public boolean isConnectedToServer() {
        return connectedToServer;
    }
}