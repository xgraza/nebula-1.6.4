package wtf.nebula.client.impl.module.miscellaneous;

import com.google.gson.JsonArray;
import me.bush.eventbus.annotation.EventListener;
import net.minecraft.client.multiplayer.ServerData;
import net.minecraft.network.play.client.C01PacketChatMessage;
import net.minecraft.network.play.server.S01PacketJoinGame;
import net.minecraft.util.EnumChatFormatting;
import wtf.nebula.client.api.config.Config;
import wtf.nebula.client.api.property.Property;
import wtf.nebula.client.impl.event.base.Era;
import wtf.nebula.client.impl.event.impl.client.EventTick;
import wtf.nebula.client.impl.event.impl.network.EventPacket;
import wtf.nebula.client.impl.module.ModuleCategory;
import wtf.nebula.client.impl.module.ToggleableModule;
import wtf.nebula.client.utils.client.Timer;
import wtf.nebula.client.utils.io.EncryptionUtils;

import java.util.ArrayList;
import java.util.List;

public class AutoLogin extends ToggleableModule {
    private static final List<LoginData> loginDataList = new ArrayList<>();
    private static Config config;

    private final Property<Integer> delay = new Property<>(5, 0, 15, "Delay", "dl");
    private final Property<Type> type = new Property<>(Type.NORMAL, "Type", "t");

    private final Timer timer = new Timer();
    private boolean login = false;

    private int setupStep = -1;
    private boolean sent = false;

    public AutoLogin() {
        super("Auto Login", new String[]{"autologin", "autolog"}, ModuleCategory.MISCELLANEOUS);
        offerProperties(delay, type);

        config = new Config("login_data.txt") {

            @Override
            public void load(String element) {
                if (element == null || element.isEmpty()) {
                    setupStep = 0;
                    return;
                }

                loginDataList.clear();

                JsonArray arr;
                try {

                } catch (Exception e) {
                    e.printStackTrace();
                    return;
                }
            }

            @Override
            public void save() {

            }
        };
    }

    @Override
    protected void onEnable() {
        super.onEnable();

        if (!config.getFile().exists()) {
            setupStep = 0;
        }
    }

    @Override
    protected void onDisable() {
        super.onDisable();

        login = false;
        setupStep = -1;
        sent = false;
    }

    @EventListener
    public void onTick(EventTick event) {
        if (login && timer.hasPassed(delay.getValue() * 1000L, false)) {
            loginDataList
                    .stream()
                    .filter((d) -> d.username.equals(mc.session.getUsername()) && d.serverIp.equals(mc.currentServerData.serverIP))
                    .findFirst()
                    .ifPresent((data) -> {
                        mc.thePlayer.sendChatMessage("/" + type.getValue().t + " " + data.password);
                        login = false;
                    });

            return;
        }

        if (setupStep == 0) {
            if (!sent) {
                sent = true;
                print("Please provide a password in chat. This will not be shown to other players.");
            }
        } else if (setupStep == 1) {
            if (!sent) {
                sent = true;
                print("Please provide a salt. This is a number anywhere from 1 to " + Integer.MAX_VALUE + ".");
            }
        }
    }

    @EventListener
    public void onPacket(EventPacket event) {
        if (!event.getEra().equals(Era.PRE)) {
            return;
        }

        if (event.getPacket() instanceof C01PacketChatMessage) {

            String message = ((C01PacketChatMessage) event.getPacket()).message;

            if (setupStep == 0) {
                event.setCancelled(true);

                EncryptionUtils.setPass(message);

                sent = false;
                setupStep = 1;
            } else if (setupStep == 1) {
                event.setCancelled(true);

                try {
                    Integer.parseInt(message);
                } catch (NumberFormatException e) {
                    print("Invalid salt. Salt must be a number.");
                    setRunning(false);
                    return;
                }

                EncryptionUtils.setSalt(message);

                setupStep = -1;
            } else {

                if (message.startsWith("/login") || message.startsWith("/register") || message.startsWith("/setpassword") || message.startsWith("/password")) {

                    ServerData serverData = mc.currentServerData;
                    if (serverData != null && !mc.isSingleplayer()) {

                        if (!hasLoginDataForServer()) {
                            String[] parts = message.split(" ");
                            if (parts.length < 2) {
                                return;
                            }

                            loginDataList.add(new LoginData(serverData.serverIP, parts[1], mc.session.getUsername()));
                            print("New login data processed for user " + EnumChatFormatting.YELLOW + mc.session.getUsername() + EnumChatFormatting.GRAY + " on this server.");
                        }
                    }

                }
            }
        } else if (event.getPacket() instanceof S01PacketJoinGame) {

            if (hasLoginDataForServer() && !login) {
                login = true;
                timer.resetTime();
            }

        }
    }

    private boolean hasLoginDataForServer() {
        ServerData serverData = mc.currentServerData;
        if (serverData != null && !mc.isSingleplayer()) {
            return loginDataList.stream().anyMatch((d) -> d.serverIp.equals(serverData.serverIP) && d.username.equals(mc.session.getUsername()));
        }

        return false;
    }

    private static class LoginData {
        private final String serverIp, password, username;

        public LoginData(String serverIp, String password, String username) {
            this.serverIp = serverIp;
            this.password = password;
            this.username = username;
        }

        public String getServerIp() {
            return serverIp;
        }

        public String getPassword() {
            return password;
        }

        public String getUsername() {
            return username;
        }
    }

    public enum Type {
        NORMAL("login"), OTHER("setpassword");

        private final String t;

        Type(String t) {
            this.t = t;
        }
    }
}
