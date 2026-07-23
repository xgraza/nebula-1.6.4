package net.minecraft.network.play.client;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C11PacketEnchantItem extends Packet
{
    private int id;
    private int button;

    public C11PacketEnchantItem()
    {
    }

    public C11PacketEnchantItem(int id, int button)
    {
        this.id = id;
        this.button = button;
    }

    public void processPacket(INetHandlerPlayServer netHandler)
    {
        netHandler.processEnchantItem(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buffer) throws IOException
    {
        this.id = buffer.readByte();
        this.button = buffer.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buffer) throws IOException
    {
        buffer.writeByte(this.id);
        buffer.writeByte(this.button);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("id=%d, button=%d", this.id, this.button);
    }

    public int getID()
    {
        return this.id;
    }

    public int getButton()
    {
        return this.button;
    }

    public void processPacket(INetHandler netHandler)
    {
        this.processPacket((INetHandlerPlayServer) netHandler);
    }
}
