package net.minecraft.network.play.server;

import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S09PacketHeldItemChange extends Packet
{
    private int slotIndex;

    public S09PacketHeldItemChange()
    {
    }

    public S09PacketHeldItemChange(int slotIndex)
    {
        this.slotIndex = slotIndex;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.slotIndex = p_148837_1_.readByte();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.slotIndex);
    }

    public void processPacket(INetHandlerPlayClient p_149386_1_)
    {
        p_149386_1_.handleHeldItemChange(this);
    }

    public int getSlotIndex()
    {
        return this.slotIndex;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
