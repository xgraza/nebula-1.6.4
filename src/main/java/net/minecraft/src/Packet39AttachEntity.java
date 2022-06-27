package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet39AttachEntity extends Packet
{
    /** 0 for riding, 1 for leashed. */
    public int attachState;
    public int ridingEntityId;
    public int vehicleEntityId;

    public Packet39AttachEntity() {}

    public Packet39AttachEntity(int par1, Entity par2Entity, Entity par3Entity)
    {
        this.attachState = par1;
        this.ridingEntityId = par2Entity.entityId;
        this.vehicleEntityId = par3Entity != null ? par3Entity.entityId : -1;
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 8;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.ridingEntityId = par1DataInput.readInt();
        this.vehicleEntityId = par1DataInput.readInt();
        this.attachState = par1DataInput.readUnsignedByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.ridingEntityId);
        par1DataOutput.writeInt(this.vehicleEntityId);
        par1DataOutput.writeByte(this.attachState);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleAttachEntity(this);
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
        Packet39AttachEntity var2 = (Packet39AttachEntity)par1Packet;
        return var2.ridingEntityId == this.ridingEntityId;
    }
}
