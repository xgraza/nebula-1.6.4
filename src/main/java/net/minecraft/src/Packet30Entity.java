package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet30Entity extends Packet
{
    /** The ID of this entity. */
    public int entityId;

    /** The X axis relative movement. */
    public byte xPosition;

    /** The Y axis relative movement. */
    public byte yPosition;

    /** The Z axis relative movement. */
    public byte zPosition;

    /** The X axis rotation. */
    public byte yaw;

    /** The Y axis rotation. */
    public byte pitch;

    /** Boolean set to true if the entity is rotating. */
    public boolean rotating;

    public Packet30Entity() {}

    public Packet30Entity(int par1)
    {
        this.entityId = par1;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.entityId = par1DataInput.readInt();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.entityId);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleEntity(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 4;
    }

    public String toString()
    {
        return "Entity_" + super.toString();
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
        Packet30Entity var2 = (Packet30Entity)par1Packet;
        return var2.entityId == this.entityId;
    }
}
