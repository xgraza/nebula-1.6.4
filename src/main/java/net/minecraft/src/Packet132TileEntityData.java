package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet132TileEntityData extends Packet
{
    /** The X position of the tile entity to update. */
    public int xPosition;

    /** The Y position of the tile entity to update. */
    public int yPosition;

    /** The Z position of the tile entity to update. */
    public int zPosition;

    /** The type of update to perform on the tile entity. */
    public int actionType;

    /**
     * NBT tile entity data. This is NOT related to writeToNBT or readFromNBT.
     */
    public NBTTagCompound data;

    public Packet132TileEntityData()
    {
        this.isChunkDataPacket = true;
    }

    public Packet132TileEntityData(int par1, int par2, int par3, int par4, NBTTagCompound par5NBTTagCompound)
    {
        this.isChunkDataPacket = true;
        this.xPosition = par1;
        this.yPosition = par2;
        this.zPosition = par3;
        this.actionType = par4;
        this.data = par5NBTTagCompound;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.xPosition = par1DataInput.readInt();
        this.yPosition = par1DataInput.readShort();
        this.zPosition = par1DataInput.readInt();
        this.actionType = par1DataInput.readByte();
        this.data = readNBTTagCompound(par1DataInput);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.xPosition);
        par1DataOutput.writeShort(this.yPosition);
        par1DataOutput.writeInt(this.zPosition);
        par1DataOutput.writeByte((byte)this.actionType);
        writeNBTTagCompound(this.data, par1DataOutput);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleTileEntityData(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 25;
    }
}
