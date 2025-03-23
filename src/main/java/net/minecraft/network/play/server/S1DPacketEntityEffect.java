package net.minecraft.network.play.server;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;
import net.minecraft.potion.PotionEffect;

public class S1DPacketEntityEffect extends Packet
{
    private int entityId;
    private byte potionId;
    private byte amplifier;
    private short duration;
    private static final String __OBFID = "CL_00001343";

    public S1DPacketEntityEffect() {}

    public S1DPacketEntityEffect(int entityId, PotionEffect effect)
    {
        this.entityId = entityId;
        this.potionId = (byte)(effect.getPotionID() & 255);
        this.amplifier = (byte)(effect.getAmplifier() & 255);

        if (effect.getDuration() > 32767)
        {
            this.duration = 32767;
        }
        else
        {
            this.duration = (short) effect.getDuration();
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.entityId = p_148837_1_.readInt();
        this.potionId = p_148837_1_.readByte();
        this.amplifier = p_148837_1_.readByte();
        this.duration = p_148837_1_.readShort();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeInt(this.entityId);
        p_148840_1_.writeByte(this.potionId);
        p_148840_1_.writeByte(this.amplifier);
        p_148840_1_.writeShort(this.duration);
    }

    public boolean isInfinite()
    {
        return this.duration == 32767;
    }

    public void processPacket(INetHandlerPlayClient p_149430_1_)
    {
        p_149430_1_.handleEntityEffect(this);
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public byte getPotionId()
    {
        return this.potionId;
    }

    public byte getAmplifier()
    {
        return this.amplifier;
    }

    public short getDuration()
    {
        return this.duration;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient)p_148833_1_);
    }
}
