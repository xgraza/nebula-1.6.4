package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet4UpdateTime extends Packet
{
    /** World age in ticks. */
    public long worldAge;

    /** The world time in minutes. */
    public long time;

    public Packet4UpdateTime() {}

    public Packet4UpdateTime(long par1, long par3, boolean par5)
    {
        this.worldAge = par1;
        this.time = par3;

        if (!par5)
        {
            this.time = -this.time;

            if (this.time == 0L)
            {
                this.time = -1L;
            }
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.worldAge = par1DataInput.readLong();
        this.time = par1DataInput.readLong();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeLong(this.worldAge);
        par1DataOutput.writeLong(this.time);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleUpdateTime(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 16;
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
        return true;
    }

    /**
     * If this returns true, the packet may be processed on any thread; otherwise it is queued for the main thread to
     * handle.
     */
    public boolean canProcessAsync()
    {
        return true;
    }
}
