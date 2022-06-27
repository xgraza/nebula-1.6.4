package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet63WorldParticles extends Packet
{
    /**
     * The name of the particle to create. A list can be found at https://gist.github.com/thinkofdeath/5110835
     */
    private String particleName;

    /** X position of the particle. */
    private float posX;

    /** Y position of the particle. */
    private float posY;

    /** Z position of the particle. */
    private float posZ;

    /**
     * This is added to the X position after being multiplied by random.nextGaussian()
     */
    private float offsetX;

    /**
     * This is added to the Y position after being multiplied by random.nextGaussian()
     */
    private float offsetY;

    /**
     * This is added to the Z position after being multiplied by random.nextGaussian()
     */
    private float offsetZ;

    /** The speed of each particle. */
    private float speed;

    /** The number of particles to create. */
    private int quantity;

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.particleName = readString(par1DataInput, 64);
        this.posX = par1DataInput.readFloat();
        this.posY = par1DataInput.readFloat();
        this.posZ = par1DataInput.readFloat();
        this.offsetX = par1DataInput.readFloat();
        this.offsetY = par1DataInput.readFloat();
        this.offsetZ = par1DataInput.readFloat();
        this.speed = par1DataInput.readFloat();
        this.quantity = par1DataInput.readInt();
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        writeString(this.particleName, par1DataOutput);
        par1DataOutput.writeFloat(this.posX);
        par1DataOutput.writeFloat(this.posY);
        par1DataOutput.writeFloat(this.posZ);
        par1DataOutput.writeFloat(this.offsetX);
        par1DataOutput.writeFloat(this.offsetY);
        par1DataOutput.writeFloat(this.offsetZ);
        par1DataOutput.writeFloat(this.speed);
        par1DataOutput.writeInt(this.quantity);
    }

    public String getParticleName()
    {
        return this.particleName;
    }

    /**
     * Gets the X position of the particle.
     */
    public double getPositionX()
    {
        return (double)this.posX;
    }

    /**
     * Gets the Y position of the particle.
     */
    public double getPositionY()
    {
        return (double)this.posY;
    }

    /**
     * Gets the Z position of the particle.
     */
    public double getPositionZ()
    {
        return (double)this.posZ;
    }

    /**
     * This is added to the X position after being multiplied by random.nextGaussian()
     */
    public float getOffsetX()
    {
        return this.offsetX;
    }

    /**
     * This is added to the Y position after being multiplied by random.nextGaussian()
     */
    public float getOffsetY()
    {
        return this.offsetY;
    }

    /**
     * This is added to the Z position after being multiplied by random.nextGaussian()
     */
    public float getOffsetZ()
    {
        return this.offsetZ;
    }

    /**
     * Gets the speed of the particles.
     */
    public float getSpeed()
    {
        return this.speed;
    }

    /**
     * Gets the number of particles to create.
     */
    public int getQuantity()
    {
        return this.quantity;
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleWorldParticles(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 64;
    }
}
