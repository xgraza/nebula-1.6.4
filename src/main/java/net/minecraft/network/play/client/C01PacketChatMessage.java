package net.minecraft.network.play.client;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C01PacketChatMessage extends Packet
{
    private String message;

    public C01PacketChatMessage()
    {
    }

    public C01PacketChatMessage(String message)
    {
        if (message.length() > 100)
        {
            message = message.substring(0, 100);
        }

        this.message = message;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer packetBuf) throws IOException
    {
        this.message = packetBuf.readStringFromBuffer(100);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer packetBuf) throws IOException
    {
        packetBuf.writeStringToBuffer(this.message);
    }

    public void processPacket(INetHandlerPlayServer handler)
    {
        handler.processChatMessage(this);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("message='%s'", this.message);
    }

    public String getMessage()
    {
        return this.message;
    }

    public void processPacket(INetHandler netHandler)
    {
        this.processPacket((INetHandlerPlayServer) netHandler);
    }
}
