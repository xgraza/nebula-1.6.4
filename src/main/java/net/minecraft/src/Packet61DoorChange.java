package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet61DoorChange extends Packet
{
    public int sfxID;
    public int auxData;
    public int posX;
    public int posY;
    public int posZ;
    private boolean disableRelativeVolume;

    public Packet61DoorChange() {}

    public Packet61DoorChange(int par1, int par2, int par3, int par4, int par5, boolean par6)
    {
        this.sfxID = par1;
        this.posX = par2;
        this.posY = par3;
        this.posZ = par4;
        this.auxData = par5;
        this.disableRelativeVolume = par6;
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.sfxID = par1DataInput.readInt();
        this.posX = par1DataInput.readInt();
        this.posY = par1DataInput.readByte() & 255;
        this.posZ = par1DataInput.readInt();
        this.auxData = par1DataInput.readInt();
        this.disableRelativeVolume = par1DataInput.readBoolean();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.sfxID);
        par1DataOutput.writeInt(this.posX);
        par1DataOutput.writeByte(this.posY & 255);
        par1DataOutput.writeInt(this.posZ);
        par1DataOutput.writeInt(this.auxData);
        par1DataOutput.writeBoolean(this.disableRelativeVolume);
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleDoorChange(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 21;
    }

    public boolean getRelativeVolumeDisabled()
    {
        return this.disableRelativeVolume;
    }
}
