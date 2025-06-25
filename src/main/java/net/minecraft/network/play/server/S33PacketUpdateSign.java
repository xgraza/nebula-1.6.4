package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

public class S33PacketUpdateSign extends Packet
{
    private int x;
    private int y;
    private int z;
    private String[] lines;

    public S33PacketUpdateSign() {}

    public S33PacketUpdateSign(int x, int y, int z, String[] lines)
    {
        this.x = x;
        this.y = y;
        this.z = z;
        this.lines = new String[] { lines[0], lines[1], lines[2], lines[3] };
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer packetBuf) throws IOException
    {
        this.x = packetBuf.readInt();
        this.y = packetBuf.readShort();
        this.z = packetBuf.readInt();
        this.lines = new String[4];

        for (int i = 0; i < 4; ++i)
        {
            this.lines[i] = packetBuf.readStringFromBuffer(15);
        }
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer packetBuf) throws IOException
    {
        packetBuf.writeInt(this.x);
        packetBuf.writeShort(this.y);
        packetBuf.writeInt(this.z);

        for (int i = 0; i < 4; ++i)
        {
            packetBuf.writeStringToBuffer(this.lines[i]);
        }
    }

    public void processPacket(INetHandlerPlayClient p_149348_1_)
    {
        p_149348_1_.handleUpdateSign(this);
    }

    public int getX()
    {
        return this.x;
    }

    public int getY()
    {
        return this.y;
    }

    public int getZ()
    {
        return this.z;
    }

    public String[] getLines()
    {
        return this.lines;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
