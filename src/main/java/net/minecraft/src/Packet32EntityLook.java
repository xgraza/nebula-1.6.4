package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet32EntityLook extends Packet30Entity
{
    public Packet32EntityLook()
    {
        this.rotating = true;
    }

    public Packet32EntityLook(int par1, byte par2, byte par3)
    {
        super(par1);
        this.yaw = par2;
        this.pitch = par3;
        this.rotating = true;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        super.readPacketData(par1DataInput);
        this.yaw = par1DataInput.readByte();
        this.pitch = par1DataInput.readByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        super.writePacketData(par1DataOutput);
        par1DataOutput.writeByte(this.yaw);
        par1DataOutput.writeByte(this.pitch);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 6;
    }
}
