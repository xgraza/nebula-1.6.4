package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet55BlockDestroy extends Packet
{
    /** Entity breaking the block */
    private int entityId;

    /** X posiiton of the block */
    private int posX;

    /** Y position of the block */
    private int posY;

    /** Z position of the block */
    private int posZ;

    /** How far destroyed this block is */
    private int destroyedStage;

    public Packet55BlockDestroy() {}

    public Packet55BlockDestroy(int par1, int par2, int par3, int par4, int par5)
    {
        this.entityId = par1;
        this.posX = par2;
        this.posY = par3;
        this.posZ = par4;
        this.destroyedStage = par5;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.entityId = par1DataInput.readInt();
        this.posX = par1DataInput.readInt();
        this.posY = par1DataInput.readInt();
        this.posZ = par1DataInput.readInt();
        this.destroyedStage = par1DataInput.readUnsignedByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.entityId);
        par1DataOutput.writeInt(this.posX);
        par1DataOutput.writeInt(this.posY);
        par1DataOutput.writeInt(this.posZ);
        par1DataOutput.write(this.destroyedStage);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleBlockDestroy(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 13;
    }

    /**
     * Gets the ID of the entity breaking the block
     */
    public int getEntityId()
    {
        return this.entityId;
    }

    /**
     * Gets the X position of the block
     */
    public int getPosX()
    {
        return this.posX;
    }

    /**
     * Gets the Y position of the block
     */
    public int getPosY()
    {
        return this.posY;
    }

    /**
     * Gets the Z position of the block
     */
    public int getPosZ()
    {
        return this.posZ;
    }

    /**
     * Gets how far destroyed this block is
     */
    public int getDestroyedStage()
    {
        return this.destroyedStage;
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
        Packet55BlockDestroy var2 = (Packet55BlockDestroy)par1Packet;
        return var2.entityId == this.entityId;
    }
}
