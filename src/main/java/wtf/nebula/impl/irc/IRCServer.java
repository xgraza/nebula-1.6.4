package wtf.nebula.impl.irc;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.glassfish.tyrus.client.ClientManager;
import wtf.nebula.util.Globals;

import javax.websocket.*;
import javax.websocket.ClientEndpointConfig.Builder;
import javax.websocket.ClientEndpointConfig.Configurator;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.*;

@ClientEndpoint
public class IRCServer extends Endpoint implements Globals {
    private Session session;
    private String userKey;

    private final Timer timer = new Timer();
    private TimerTask timerTask;

    public IRCServer() {

        ClientManager manager = ClientManager.createClient();

        Builder builder = ClientEndpointConfig.Builder.create();
        builder.configurator(new Configurator() {
            @Override
            public void beforeRequest(Map<String, List<String>> headers) {
                headers.put("User-Agent", Collections.singletonList("Custom WS client"));
            }
        });

        try {
            // TODO: i'll use different hosting soon xd
            manager.connectToServer(this, builder.build(), new URI("wss://brick-iron-shift.glitch.me"));
        } catch (DeploymentException | IOException | URISyntaxException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onOpen(Session session, EndpointConfig config) {
        this.session = session;

        session.addMessageHandler(String.class, this::onMessage);

        System.out.println("[IRC] Connected to IRC");

        JsonObject object = new JsonObject();
        object.addProperty("op", OpCodes.CONNECT);
        object.addProperty("d", mc.session.getUsername());

        sendMessage(object.toString());

        // create task timeout for pinging the IRC server
        timer.schedule(timerTask = new TimerTask() {
            @Override
            public void run() {
                JsonObject ping = new JsonObject();

                ping.addProperty("op", OpCodes.PING);
                ping.addProperty("d", "");

                sendMessage(ping.toString());
            }
        }, 20L * 1000L);

        sendIRCMessage("Connected to IRC");
    }

    private void onMessage(String message) {
        JsonObject object = new JsonParser().parse(message).getAsJsonObject();

        if (object.has("op") && object.has("d")) {
            int opCode = object.get("op").getAsInt();
            String data = object.get("d").getAsString();

            switch (opCode) {
                case OpCodes.ID:
                    userKey = data;
                    System.out.println("[IRC] User key received.");
                    break;

                case OpCodes.MESSAGE_RECEIVE:
                    sendIRCMessage(data);
                    break;

                case OpCodes.DISCONNECTED:
                    sendIRCMessage("Disconnected from IRC: " + data);
                    break;
            }
        }
    }

    @Override
    public void onClose(Session session, CloseReason closeReason) {
        sendIRCMessage("You were disconnected from the IRC chat.");
        System.out.println("[IRC] Disconnected. " + closeReason.getCloseCode() + ", reason: " + closeReason.getReasonPhrase());
    }

    public void disconnect() {
        try {

            if (timerTask != null) {
                timerTask.cancel();
            }

            timer.cancel();
            timer.purge();

            if (session != null) {
                session.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void sendMessage(String msg) {
        if (session.isOpen()) {
            session.getAsyncRemote().sendText(msg);
        }

        else {
            sendIRCMessage("You are not currently connected to the IRC server");
        }
    }

    public void sendIRCChatMessage(String message) {
        JsonObject object = new JsonObject();
        object.addProperty("op", OpCodes.MESSAGE_SEND);
        object.addProperty("d", message);

        sendMessage(object.toString());
    }
}
