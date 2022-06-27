package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet31RelEntityMove extends Packet30Entity
{
    public Packet31RelEntityMove() {}

    public Packet31RelEntityMove(int par1, byte par2, byte par3, byte par4)
    {
        super(par1);
        this.xPosition = par2;
        this.yPosition = par3;
        this.zPosition = par4;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        super.readPacketData(par1DataInput);
        this.xPosition = par1DataInput.readByte();
        this.yPosition = par1DataInput.readByte();
        this.zPosition = par1DataInput.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        super.writePacketData(par1DataOutput);
        par1DataOutput.writeByte(this.xPosition);
        par1DataOutput.writeByte(this.yPosition);
        par1DataOutput.writeByte(this.zPosition);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 7;
    }
}
