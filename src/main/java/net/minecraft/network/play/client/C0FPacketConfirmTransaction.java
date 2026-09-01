package net.minecraft.network.play.client;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C0FPacketConfirmTransaction extends Packet
{
    private int id;
    private short uid;
    private boolean accepted;
    private static final String __OBFID = "CL_00001351";

    public C0FPacketConfirmTransaction()
    {
    }

    public C0FPacketConfirmTransaction(int p_i45244_1_, short p_i45244_2_, boolean p_i45244_3_)
    {
        this.id = p_i45244_1_;
        this.uid = p_i45244_2_;
        this.accepted = p_i45244_3_;
    }

    public void processPacket(INetHandlerPlayServer p_149531_1_)
    {
        p_149531_1_.processConfirmTransaction(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.id = p_148837_1_.readByte();
        this.uid = p_148837_1_.readShort();
        this.accepted = p_148837_1_.readByte() != 0;
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.id);
        p_148840_1_.writeShort(this.uid);
        p_148840_1_.writeByte(this.accepted ? 1 : 0);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("id=%d, uid=%d, accepted=%b", Integer.valueOf(this.id), Short.valueOf(this.uid), Boolean.valueOf(this.accepted));
    }

    public int func_149532_c()
    {
        return this.id;
    }

    public short func_149533_d()
    {
        return this.uid;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayServer) p_148833_1_);
    }
}
