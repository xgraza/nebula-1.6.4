package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet33RelEntityMoveLook extends Packet30Entity
{
    public Packet33RelEntityMoveLook()
    {
        this.rotating = true;
    }

    public Packet33RelEntityMoveLook(int par1, byte par2, byte par3, byte par4, byte par5, byte par6)
    {
        super(par1);
        this.xPosition = par2;
        this.yPosition = par3;
        this.zPosition = par4;
        this.yaw = par5;
        this.pitch = par6;
        this.rotating = true;
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
        this.yaw = par1DataInput.readByte();
        this.pitch = par1DataInput.readByte();
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
        par1DataOutput.writeByte(this.yaw);
        par1DataOutput.writeByte(this.pitch);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 9;
    }
}
