package net.minecraft.src;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public class Packet23VehicleSpawn extends Packet
{
    /** Entity ID of the object. */
    public int entityId;

    /** The X position of the object. */
    public int xPosition;

    /** The Y position of the object. */
    public int yPosition;

    /** The Z position of the object. */
    public int zPosition;

    /**
     * Not sent if the thrower entity ID is 0. The speed of this fireball along the X axis.
     */
    public int speedX;

    /**
     * Not sent if the thrower entity ID is 0. The speed of this fireball along the Y axis.
     */
    public int speedY;

    /**
     * Not sent if the thrower entity ID is 0. The speed of this fireball along the Z axis.
     */
    public int speedZ;

    /** The pitch in steps of 2p/256 */
    public int pitch;

    /** The yaw in steps of 2p/256 */
    public int yaw;

    /** The type of object. */
    public int type;

    /** 0 if not a fireball. Otherwise, this is the Entity ID of the thrower. */
    public int throwerEntityId;

    public Packet23VehicleSpawn() {}

    public Packet23VehicleSpawn(Entity par1Entity, int par2)
    {
        this(par1Entity, par2, 0);
    }

    public Packet23VehicleSpawn(Entity par1Entity, int par2, int par3)
    {
        this.entityId = par1Entity.entityId;
        this.xPosition = MathHelper.floor_double(par1Entity.posX * 32.0D);
        this.yPosition = MathHelper.floor_double(par1Entity.posY * 32.0D);
        this.zPosition = MathHelper.floor_double(par1Entity.posZ * 32.0D);
        this.pitch = MathHelper.floor_float(par1Entity.rotationPitch * 256.0F / 360.0F);
        this.yaw = MathHelper.floor_float(par1Entity.rotationYaw * 256.0F / 360.0F);
        this.type = par2;
        this.throwerEntityId = par3;

        if (par3 > 0)
        {
            double var4 = par1Entity.motionX;
            double var6 = par1Entity.motionY;
            double var8 = par1Entity.motionZ;
            double var10 = 3.9D;

            if (var4 < -var10)
            {
                var4 = -var10;
            }

            if (var6 < -var10)
            {
                var6 = -var10;
            }

            if (var8 < -var10)
            {
                var8 = -var10;
            }

            if (var4 > var10)
            {
                var4 = var10;
            }

            if (var6 > var10)
            {
                var6 = var10;
            }

            if (var8 > var10)
            {
                var8 = var10;
            }

            this.speedX = (int)(var4 * 8000.0D);
            this.speedY = (int)(var6 * 8000.0D);
            this.speedZ = (int)(var8 * 8000.0D);
        }
    }

    /**
     * Abstract. Reads the raw packet data from the data stream.
     */
    public void readPacketData(DataInput par1DataInput) throws IOException
    {
        this.entityId = par1DataInput.readInt();
        this.type = par1DataInput.readByte();
        this.xPosition = par1DataInput.readInt();
        this.yPosition = par1DataInput.readInt();
        this.zPosition = par1DataInput.readInt();
        this.pitch = par1DataInput.readByte();
        this.yaw = par1DataInput.readByte();
        this.throwerEntityId = par1DataInput.readInt();

        if (this.throwerEntityId > 0)
        {
            this.speedX = par1DataInput.readShort();
            this.speedY = par1DataInput.readShort();
            this.speedZ = par1DataInput.readShort();
        }
    }

    /**
     * Abstract. Writes the raw packet data to the data stream.
     */
    public void writePacketData(DataOutput par1DataOutput) throws IOException
    {
        par1DataOutput.writeInt(this.entityId);
        par1DataOutput.writeByte(this.type);
        par1DataOutput.writeInt(this.xPosition);
        par1DataOutput.writeInt(this.yPosition);
        par1DataOutput.writeInt(this.zPosition);
        par1DataOutput.writeByte(this.pitch);
        par1DataOutput.writeByte(this.yaw);
        par1DataOutput.writeInt(this.throwerEntityId);

        if (this.throwerEntityId > 0)
        {
            par1DataOutput.writeShort(this.speedX);
            par1DataOutput.writeShort(this.speedY);
            par1DataOutput.writeShort(this.speedZ);
        }
    }

    /**
     * Passes this Packet on to the NetHandler for processing.
     */
    public void processPacket(NetHandler par1NetHandler)
    {
        par1NetHandler.handleVehicleSpawn(this);
    }

    /**
     * Abstract. Return the size of the packet (not counting the header).
     */
    public int getPacketSize()
    {
        return 21 + this.throwerEntityId > 0 ? 6 : 0;
    }
}
