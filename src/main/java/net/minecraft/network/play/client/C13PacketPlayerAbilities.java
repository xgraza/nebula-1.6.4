package net.minecraft.network.play.client;

import net.minecraft.entity.player.PlayerCapabilities;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

import java.io.IOException;

public class C13PacketPlayerAbilities extends Packet
{
    private boolean invulnerable;
    private boolean flying;
    private boolean allowFlying;
    private boolean creativeMode;
    private float flySpeed;
    private float walkSpeed;

    public C13PacketPlayerAbilities(PlayerCapabilities capabilities)
    {
        setInvulnerable(capabilities.disableDamage);
        setFlying(capabilities.isFlying);
        setAllowFlying(capabilities.allowFlying);
        setCreativeMode(capabilities.isCreativeMode);
        setFlySpeed(capabilities.getFlySpeed());
        setWalkSpeed(capabilities.getWalkSpeed());
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buffer) throws IOException
    {
        byte flags = buffer.readByte();
        setInvulnerable((flags & 1) > 0);
        setFlying((flags & 2) > 0);
        setAllowFlying((flags & 4) > 0);
        setCreativeMode((flags & 8) > 0);
        setFlySpeed(buffer.readFloat());
        setWalkSpeed(buffer.readFloat());
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buffer) throws IOException
    {
        byte flags = 0;

        if (isInvulnerable())
        {
            flags = (byte) (flags | 1);
        }

        if (isFlying())
        {
            flags = (byte) (flags | 2);
        }

        if (isAllowedToFly())
        {
            flags = (byte) (flags | 4);
        }

        if (isCreativeMode())
        {
            flags = (byte) (flags | 8);
        }

        buffer.writeByte(flags);
        buffer.writeFloat(flySpeed);
        buffer.writeFloat(walkSpeed);
    }

    public void processPacket(INetHandlerPlayServer netHandler)
    {
        netHandler.processPlayerAbilities(this);
    }

    public void processPacket(INetHandler netHandler)
    {
        processPacket((INetHandlerPlayServer) netHandler);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return String.format("invuln=%b, flying=%b, canfly=%b, instabuild=%b, flyspeed=%.4f, walkspped=%.4f", new Object[]{ isInvulnerable(), isFlying(), isAllowedToFly(), isCreativeMode(), getFlySpeed(), getWalkSpeed() });
    }

    public boolean isInvulnerable()
    {
        return invulnerable;
    }

    public void setInvulnerable(boolean p_149490_1_)
    {
        invulnerable = p_149490_1_;
    }

    public boolean isFlying()
    {
        return flying;
    }

    public void setFlying(boolean p_149483_1_)
    {
        flying = p_149483_1_;
    }

    public boolean isAllowedToFly()
    {
        return allowFlying;
    }

    public void setAllowFlying(boolean p_149491_1_)
    {
        allowFlying = p_149491_1_;
    }

    public boolean isCreativeMode()
    {
        return creativeMode;
    }

    public void setCreativeMode(boolean p_149493_1_)
    {
        creativeMode = p_149493_1_;
    }

    public float getFlySpeed()
    {
        return flySpeed;
    }

    public void setFlySpeed(float p_149485_1_)
    {
        flySpeed = p_149485_1_;
    }

    public float getWalkSpeed()
    {
        return walkSpeed;
    }

    public void setWalkSpeed(float p_149492_1_)
    {
        walkSpeed = p_149492_1_;
    }
}
