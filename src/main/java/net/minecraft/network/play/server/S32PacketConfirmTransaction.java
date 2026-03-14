package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S32PacketConfirmTransaction extends Packet
{
    private int id;
    private short uid;
    private boolean accepted;

    public S32PacketConfirmTransaction()
    {
    }

    public S32PacketConfirmTransaction(int id, short uid, boolean accepted)
    {
        this.id = id;
        this.uid = uid;
        this.accepted = accepted;
    }

    public void processPacket(INetHandlerPlayClient p_148891_1_)
    {
        p_148891_1_.handleConfirmTransaction(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.id = p_148837_1_.readUnsignedByte();
        this.uid = p_148837_1_.readShort();
        this.accepted = p_148837_1_.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.id);
        p_148840_1_.writeShort(this.uid);
        p_148840_1_.writeBoolean(this.accepted);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("id=%d, uid=%d, accepted=%b", Integer.valueOf(this.id), Short.valueOf(this.uid), Boolean.valueOf(this.accepted));
    }

    public int getID()
    {
        return this.id;
    }

    public short getUID()
    {
        return this.uid;
    }

    public boolean isAccepted()
    {
        return this.accepted;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
