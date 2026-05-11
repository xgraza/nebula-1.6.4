package net.minecraft.client.multiplayer;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiDisconnected;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.network.NetHandlerLoginClient;
import net.minecraft.client.resources.I18n;
import net.minecraft.network.EnumConnectionState;
import net.minecraft.network.NetworkManager;
import net.minecraft.network.handshake.client.C00Handshake;
import net.minecraft.network.login.client.C00PacketLoginStart;
import net.minecraft.util.ChatComponentText;
import net.minecraft.util.ChatComponentTranslation;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.nebula.client.cheat.impl.player.AutoReconnectCheat;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.concurrent.atomic.AtomicInteger;

public class GuiConnecting extends GuiScreen
{
    private static final AtomicInteger CONNECTOR_THREAD_ID = new AtomicInteger(0);
    private static final Logger LOGGER = LogManager.getLogger();

    private NetworkManager networkManager;
    private boolean canceled;
    private final GuiScreen parent;

    public GuiConnecting(GuiScreen parent, Minecraft client, ServerData serverData)
    {
        this.mc = client;
        this.parent = parent;
        ServerAddress address = ServerAddress.resolveAddress(serverData.serverIP);
        client.loadWorld(null);
        client.setServerData(serverData);
        this.connectTo(address.getIP(), address.getPort());
    }

    public GuiConnecting(GuiScreen parent, Minecraft client, String host, int port)
    {
        this.mc = client;
        this.parent = parent;
        client.loadWorld(null);
        client.setServerData(new ServerData("", host + ":" + port));
        this.connectTo(host, port);
    }

    private void connectTo(final String ip, final int port)
    {
        LOGGER.info("Connecting to {}:{}", ip, port);
        new Thread(() ->
        {
            try
            {
                if (canceled)
                {
                    return;
                }

                networkManager = NetworkManager.provideLanClient(InetAddress.getByName(ip), port);
                networkManager.setNetHandler(new NetHandlerLoginClient(networkManager, mc, parent));
                networkManager.scheduleOutboundPacket(new C00Handshake(4, ip, port, EnumConnectionState.LOGIN));
                networkManager.scheduleOutboundPacket(new C00PacketLoginStart(mc.getSession().getGameProfile()));
            } catch (final UnknownHostException var2)
            {
                if (canceled)
                {
                    return;
                }

                LOGGER.error("Couldn't connect to server", var2);
                mc.displayGuiScreen(new GuiDisconnected(parent,
                        "connect.failed",
                        new ChatComponentTranslation(
                                "disconnect.genericReason",
                                "Unknown host '" + ip + "'")));
            } catch (final Exception exception)
            {
                if (canceled)
                {
                    return;
                }

                LOGGER.error("Couldn't connect to server", exception);
                mc.displayGuiScreen(new GuiDisconnected(this.parent,
                        "connect.failed",
                        new ChatComponentTranslation(
                                "disconnect.genericReason",
                                exception.toString())));
            }
        }, "Server Connector #" + CONNECTOR_THREAD_ID.incrementAndGet()).start();
    }

    /**
     * Called from the main game loop to update the screen.
     */
    public void updateScreen()
    {
        if (this.networkManager != null)
        {
            if (this.networkManager.isChannelOpen())
            {
                this.networkManager.processReceivedPackets();
            } else if (this.networkManager.getExitMessage() != null)
            {
                this.networkManager.getNetHandler().onDisconnect(this.networkManager.getExitMessage());
            }
        }
    }

    /**
     * Fired when a key is typed. This is the equivalent of KeyListener.keyTyped(KeyEvent e).
     */
    protected void keyTyped(char typedChar, int keyCode)
    {
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        this.buttonList.clear();
        this.buttonList.add(new GuiButton(0, this.width / 2 - 100, this.height / 4 + 120 + 12, I18n.format("gui.cancel")));
    }

    protected void actionPerformed(GuiButton button)
    {
        if (button.id == 0)
        {
            this.canceled = true;

            if (this.networkManager != null)
            {
                this.networkManager.closeChannel(new ChatComponentText("Aborted"));
            }

            this.mc.displayGuiScreen(this.parent);
        }
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        this.drawDefaultBackground();

        if (this.networkManager == null)
        {
            this.drawCenteredString(this.fontRenderer, I18n.format("connect.connecting"), this.width / 2, this.height / 2 - 50, 16777215);
        } else
        {
            this.drawCenteredString(this.fontRenderer, I18n.format("connect.authorizing"), this.width / 2, this.height / 2 - 50, 16777215);
        }

        super.drawScreen(par1, par2, par3);
    }
}
