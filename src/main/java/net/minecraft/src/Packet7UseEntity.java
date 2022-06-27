package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet7UseEntity extends Packet
{
    /** The entity of the player (ignored by the server) */
    public int playerEntityId;

    /** The entity the player is interacting with */
    public int targetEntity;

    /**
     * Seems to be true when the player is pointing at an entity and left-clicking and false when right-clicking.
     */
    public int isLeftClick;

    public Packet7UseEntity() {}

    public Packet7UseEntity(int par1, int par2, int par3)
    {
        this.playerEntityId = par1;
        this.targetEntity = par2;
        this.isLeftClick = par3;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.playerEntityId = par1DataInput.readInt();
        this.targetEntity = par1DataInput.readInt();
        this.isLeftClick = par1DataInput.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.playerEntityId);
        par1DataOutput.writeInt(this.targetEntity);
        par1DataOutput.writeByte(this.isLeftClick);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleUseEntity(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 9;
    }
}
