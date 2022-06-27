package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet12PlayerLook extends Packet10Flying
{
    public Packet12PlayerLook()
    {
        this.rotating = true;
    }

    public Packet12PlayerLook(float par1, float par2, boolean par3)
    {
        this.yaw = par1;
        this.pitch = par2;
        this.onGround = par3;
        this.rotating = true;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.yaw = par1DataInput.readFloat();
        this.pitch = par1DataInput.readFloat();
        super.readPacketData(par1DataInput);
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeFloat(this.yaw);
        par1DataOutput.writeFloat(this.pitch);
        super.writePacketData(par1DataOutput);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 9;
    }
}
