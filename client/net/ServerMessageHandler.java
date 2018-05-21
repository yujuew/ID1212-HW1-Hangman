package client.net;

import shared.Message;

public interface ServerMessageHandler {
    void handleMsg(Message msg);
}
