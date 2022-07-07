package wtf.nebula.impl.irc;

public interface OpCodes {
    int DISCONNECTED = 6969;
    int INVALID = 69420;

    int ID = -1;
    int CONNECT = 0;
    int MESSAGE_SEND = 1;
    int MESSAGE_RECEIVE = 2;
    int PING = 90;
}
