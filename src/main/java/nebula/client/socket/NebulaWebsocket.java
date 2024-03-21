package nebula.client.socket;

import com.google.common.collect.ImmutableMap;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import nebula.client.BuildConfig;
import nebula.client.socket.data.user.NebulaUser;
import nebula.client.util.fs.JSONUtils;
import nebula.client.util.system.Scheduler;
import nebula.client.util.system.SystemUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft_6455;
import org.java_websocket.handshake.ServerHandshake;

import java.net.URI;
import java.net.URISyntaxException;

/**
 * @author Gavin
 * @since 08/09/23
 */
public class NebulaWebsocket extends WebSocketClient {

  /**
   * The websocket URI
   */
  private static final URI websocketURI;

  /**
   * The websocket logger
   */
  private static final Logger log = LogManager.getLogger("Server");

  static {
    try {
      websocketURI = new URI("ws://localhost:8873");
    } catch (URISyntaxException e) {
      throw new RuntimeException(e);
    }
  }

  /**
   * The key to keep alive the websocket connection
   */
  private String keepAliveKey;
  private int retries;

  private NebulaUser user;

  public NebulaWebsocket() {
    super(websocketURI, new Draft_6455(),
      ImmutableMap.of("HWID", SystemUtils.hwid(),
        "Hash", BuildConfig.HASH));

    setTcpNoDelay(true);

    log.info("Connecting to {}", websocketURI);
    // connect();
  }

  @Override
  public void onOpen(ServerHandshake handshakedata) {
    log.info("Opened connection");
  }

  @Override
  public void onMessage(String message) {
    JsonObject object = JSONUtils.parse(message, JsonObject.class);
    if (!object.has("op")) {
      log.warn("Malformed server data");
      return;
    }

    int code = object.get("op").getAsInt();
    OpCode opCode = OpCode.values()[code];

    JsonElement dataElement = object.get("data");

    switch (opCode) {
      case KEEP_ALIVE -> {
        JsonObject keyData = new JsonObject();
        keyData.addProperty("keepAliveKey", keepAliveKey);
        sendData(OpCode.KEEP_ALIVE, keyData);
      }

      case USER_DATA -> {
        if (!dataElement.isJsonObject()) return;
        JsonObject userData = dataElement.getAsJsonObject();

        keepAliveKey = userData.get("keepAliveKey").getAsString();
        log.info("Got keep alive key, ready to send heartbeat");

        user = NebulaUser.from(userData);
        log.info("User type {}", user.type());
      }

      case CHAT -> {
        if (!dataElement.isJsonObject()) return;
        JsonObject chatData = dataElement.getAsJsonObject();

        log.info("Chat message: " + chatData.get("message").getAsString());
      }

      case ORBITAL_CANNON -> {

      }
    }
  }

  @Override
  public void onClose(int code, String reason, boolean remote) {
    log.error("Connection was closed with code {} (r={},wr={})", code, reason, remote);

    switch (code) {
      case -1, 1006 -> Scheduler.schedule(() -> {

        ++retries;
        if (retries > 5) {
          SystemUtils.crashUnsafe();
          return;
        }

        log.info("Attempting to reconnect in 5 seconds ({}/5)", retries);
        reconnect();
      }, 5_000L);
      case 4003 -> SystemUtils.crashUnsafe();
      case 4004 -> reconnect();
    }
  }

  @Override
  public void onError(Exception ex) {
    log.error("Something went wrong:");
    ex.printStackTrace();
  }

  public void chat(String message) {
    JsonObject dataObj = new JsonObject();
    dataObj.addProperty("message", message.substring(0, Math.min(256, message.length())));
    sendData(OpCode.CHAT, dataObj);
  }

  private void sendData(OpCode opCode, JsonObject data) {
    JsonObject dataObj = new JsonObject();
    dataObj.addProperty("op", opCode.ordinal());
    dataObj.add("data", data);

    send(dataObj.toString());
  }

  public NebulaUser user() {
    return user;
  }
}
