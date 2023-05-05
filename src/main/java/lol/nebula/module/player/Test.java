package lol.nebula.module.player;

import com.google.common.base.Charsets;
import com.mojang.authlib.GameProfile;
import lol.nebula.listener.bus.Listener;
import lol.nebula.listener.events.entity.EventUpdate;
import lol.nebula.module.Module;
import lol.nebula.module.ModuleCategory;
import lol.nebula.setting.Setting;
import lol.nebula.util.feature.DevelopmentFeature;
import lol.nebula.util.math.MathUtils;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.Proxy;
import java.net.Socket;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.CopyOnWriteArrayList;

@DevelopmentFeature
public class Test extends Module {

    private static final String HOST = "alfheim.pw";
    private static final int PORT = 25565;

    private final Setting<Integer> accounts = new Setting<>(10, 1, 50, "Accounts");

    private final List<Bot> bots = new CopyOnWriteArrayList<>();

    public Test() {
        super("Test", "lol", ModuleCategory.PLAYER);
    }

    @Override
    public void onEnable() {
        super.onEnable();

        if (mc.theWorld == null) return;

        for (int i = 0; i < accounts.getValue(); ++i) {
            String random = "";
            for (int x = 0; x < 10; ++x) {
                random += (char) (MathUtils.getRNG().nextInt(90 - 65) + 65);
            }
            Bot bot = new Bot(new GameProfile(UUID.randomUUID().toString(), random));
            try {
                print("Connecting bot " + random);
                bot.connect();
                bots.add(bot);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onDisable() {
        super.onDisable();

        if (!bots.isEmpty()) {
            bots.forEach((bot) -> {
                try {
                    bot.disconnect();
                } catch (IOException ignored) {

                }
            });
        }

        bots.clear();
    }

    @Listener
    public void onUpdate(EventUpdate event) {
        if (bots.isEmpty()) return;

        for (Bot bot : bots) {
            try {
                bot.onTick();
            } catch (Exception ignored) {

            }
        }
    }

    private static class Bot {
        private Socket socket;
        private final GameProfile profile;

        private boolean login;

        public Bot(GameProfile profile) {
            this.profile = profile;
        }

        public void connect() throws IOException {
            if (socket == null) {
                socket = new Socket(Proxy.NO_PROXY);
                socket.setTcpNoDelay(true);
                socket.setKeepAlive(true);
                socket.connect(new InetSocketAddress(HOST, PORT));
            }
        }

        public void disconnect() throws IOException {
            if (socket == null || !socket.isConnected()) return;

            socket.close();
            socket = null;
        }

        public void onTick() throws IOException {
            if (!socket.isConnected()) return;

            if (!login) {
                login = true;
                writePacketData(handshake());
                writePacketData(login());
                return;
            }

        }

        private byte[] chat(String message) throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeByte(0x02);
            writeStringToBuffer(dos, message);

            return baos.toByteArray();
        }

        private byte[] handshake() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeByte(0x00);
            writeVarIntToBuffer(dos, 4);
            writeStringToBuffer(dos, HOST);
            dos.writeShort(PORT);
            writeVarIntToBuffer(dos, 2);

            return baos.toByteArray();
        }

        private byte[] login() throws IOException {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);

            dos.writeByte(0x00);
            writeStringToBuffer(dos, profile.getName());

            return baos.toByteArray();
        }

        private void writePacketData(byte[] data) throws IOException {
            DataOutputStream output = new DataOutputStream(socket.getOutputStream());
            writeVarIntToBuffer(output, data.length);
            output.write(data, 0, data.length);
        }

        private void writeVarIntToBuffer(DataOutputStream dos, int var) throws IOException {
            while ((var & -128) != 0) {
                dos.writeByte(var & 127 | 128);
                var >>>= 7;
            }

            dos.writeByte(var);
        }

        /**
         * Writes a (UTF-8 encoded) String to this buffer. Will throw IOException if String length exceeds 32767 bytes
         */
        private void writeStringToBuffer(DataOutputStream dos, String p_150785_1_) throws IOException
        {
            byte[] var2 = p_150785_1_.getBytes(Charsets.UTF_8);

            if (var2.length > 32767)
            {
                throw new IOException("String too big (was " + p_150785_1_.length() + " bytes encoded, max " + 32767 + ")");
            }
            else
            {
                writeVarIntToBuffer(dos, var2.length);
                dos.write(var2);
            }
        }
    }
}
