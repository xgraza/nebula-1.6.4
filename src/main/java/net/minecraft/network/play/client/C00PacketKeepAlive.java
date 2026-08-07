package net.minecraft.network.play.client;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C00PacketKeepAlive extends Packet
{
    private int key;

    public C00PacketKeepAlive()
    {
    }

    public C00PacketKeepAlive(int key)
    {
        this.key = key;
    }

    public void processPacket(INetHandlerPlayServer p_149459_1_)
    {
        p_149459_1_.processKeepAlive(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.key = p_148837_1_.readInt();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.key);
    }

    /**
     * If true, the network manager will process the packet immediately when received, otherwise it will queue it for
     * processing. Currently true for: Disconnect, LoginSuccess, KeepAlive, ServerQuery/Info, Ping/Pong
     */
    public boolean hasPriority()
    {
        return true;
    }

    public int getKey()
    {
        return this.key;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
