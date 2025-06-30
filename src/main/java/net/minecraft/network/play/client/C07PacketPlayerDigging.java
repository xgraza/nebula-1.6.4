package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;
import net.minecraft.src.BlockPos;

public class C07PacketPlayerDigging extends Packet
{
    private int x;
    private int y;
    private int z;
    private int side;
    private int action;

    public C07PacketPlayerDigging() {}

    public C07PacketPlayerDigging(int action, BlockPos pos, int face)
    {
        this.action = action;
        this.x = pos.getX();
        this.y = pos.getY();
        this.z = pos.getZ();
        this.side = face;
    }

    public C07PacketPlayerDigging(int action, int p_i45258_2_, int p_i45258_3_, int p_i45258_4_, int p_i45258_5_)
    {
        this.action = action;
        this.x = p_i45258_2_;
        this.y = p_i45258_3_;
        this.z = p_i45258_4_;
        this.side = p_i45258_5_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.action = p_148837_1_.readUnsignedByte();
        this.x = p_148837_1_.readInt();
        this.y = p_148837_1_.readUnsignedByte();
        this.z = p_148837_1_.readInt();
        this.side = p_148837_1_.readUnsignedByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.action);
        p_148840_1_.writeInt(this.x);
        p_148840_1_.writeByte(this.y);
        p_148840_1_.writeInt(this.z);
        p_148840_1_.writeByte(this.side);
    }

    public void processPacket(INetHandlerPlayServer p_149504_1_)
    {
        p_149504_1_.processPlayerDigging(this);
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

    public int getSide()
    {
        return this.side;
    }

    public int getAction()
    {
        return this.action;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayServer)p_148833_1_);
    }
}
