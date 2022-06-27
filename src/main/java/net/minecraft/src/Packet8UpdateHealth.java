package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet8UpdateHealth extends Packet
{
    /** Variable used for incoming health packets */
    public float healthMP;
    public int food;

    /**
     * Players logging on get a saturation of 5.0. Eating food increases the saturation as well as the food bar.
     */
    public float foodSaturation;

    public Packet8UpdateHealth() {}

    public Packet8UpdateHealth(float par1, int par2, float par3)
    {
        this.healthMP = par1;
        this.food = par2;
        this.foodSaturation = par3;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.healthMP = par1DataInput.readFloat();
        this.food = par1DataInput.readShort();
        this.foodSaturation = par1DataInput.readFloat();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeFloat(this.healthMP);
        par1DataOutput.writeShort(this.food);
        par1DataOutput.writeFloat(this.foodSaturation);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleUpdateHealth(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8;
    }

    /**
     * only false for the abstract Packet class, all real packets return true
     */
    public boolean isRealPacket()
    {
        return true;
    }

    /**
     * eg return packet30entity.entityId == entityId; WARNING : will throw if you compare a packet to a different packet
     * class
     */
    public boolean containsSameEntityIDAs(Packet par1Packet)
    {
        return true;
    }
}
