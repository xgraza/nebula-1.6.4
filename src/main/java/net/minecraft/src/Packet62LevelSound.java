package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet62LevelSound extends Packet
{
    /** e.g. step.grass */
    private String soundName;

    /** Effect X multiplied by 8 */
    private int effectX;

    /** Effect Y multiplied by 8 */
    private int effectY = Integer.MAX_VALUE;

    /** Effect Z multiplied by 8 */
    private int effectZ;

    /** 1 is 100%. Can be more. */
    private float volume;

    /** 63 is 100%. Can be more. */
    private int pitch;

    public Packet62LevelSound() {}

    public Packet62LevelSound(String par1Str, double par2, double par4, double par6, float par8, float par9)
    {
        this.soundName = par1Str;
        this.effectX = (int)(par2 * 8.0D);
        this.effectY = (int)(par4 * 8.0D);
        this.effectZ = (int)(par6 * 8.0D);
        this.volume = par8;
        this.pitch = (int)(par9 * 63.0F);

        if (this.pitch < 0)
        {
            this.pitch = 0;
        }

        if (this.pitch > 255)
        {
            this.pitch = 255;
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.soundName = readString(par1DataInput, 256);
        this.effectX = par1DataInput.readInt();
        this.effectY = par1DataInput.readInt();
        this.effectZ = par1DataInput.readInt();
        this.volume = par1DataInput.readFloat();
        this.pitch = par1DataInput.readUnsignedByte();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        writeString(this.soundName, par1DataOutput);
        par1DataOutput.writeInt(this.effectX);
        par1DataOutput.writeInt(this.effectY);
        par1DataOutput.writeInt(this.effectZ);
        par1DataOutput.writeFloat(this.volume);
        par1DataOutput.writeByte(this.pitch);
    }

    public String getSoundName()
    {
        return this.soundName;
    }

    public double getEffectX()
    {
        return (double)((float)this.effectX / 8.0F);
    }

    public double getEffectY()
    {
        return (double)((float)this.effectY / 8.0F);
    }

    public double getEffectZ()
    {
        return (double)((float)this.effectZ / 8.0F);
    }

    public float getVolume()
    {
        return this.volume;
    }

    /**
     * Gets the pitch divided by 63 (63 is 100%)
     */
    public float getPitch()
    {
        return (float)this.pitch / 63.0F;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleLevelSound(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 24;
    }
}
