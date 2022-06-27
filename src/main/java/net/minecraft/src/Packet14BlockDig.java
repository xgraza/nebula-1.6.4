package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet14BlockDig extends Packet
{
    /** Block X position. */
    public int xPosition;

    /** Block Y position. */
    public int yPosition;

    /** Block Z position. */
    public int zPosition;

    /** Punched face of the block. */
    public int face;

    /** Status of the digging (started, ongoing, broken). */
    public int status;

    public Packet14BlockDig() {}

    public Packet14BlockDig(int par1, int par2, int par3, int par4, int par5)
    {
        this.status = par1;
        this.xPosition = par2;
        this.yPosition = par3;
        this.zPosition = par4;
        this.face = par5;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.status = par1DataInput.readUnsignedByte();
        this.xPosition = par1DataInput.readInt();
        this.yPosition = par1DataInput.readUnsignedByte();
        this.zPosition = par1DataInput.readInt();
        this.face = par1DataInput.readUnsignedByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.write(this.status);
        par1DataOutput.writeInt(this.xPosition);
        par1DataOutput.write(this.yPosition);
        par1DataOutput.writeInt(this.zPosition);
        par1DataOutput.write(this.face);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleBlockDig(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 11;
    }
}
