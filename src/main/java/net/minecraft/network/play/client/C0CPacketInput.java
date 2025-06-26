package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0CPacketInput extends Packet
{
    private float movingStrafing;
    private float moveForward;
    private boolean jump;
    private boolean sneak;

    public C0CPacketInput() {}

    public C0CPacketInput(float moveStrafing, float moveForward, boolean jump, boolean sneak)
    {
        this.movingStrafing = moveStrafing;
        this.moveForward = moveForward;
        this.jump = jump;
        this.sneak = sneak;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer packetBuf) throws IOException
    {
        this.movingStrafing = packetBuf.readFloat();
        this.moveForward = packetBuf.readFloat();
        this.jump = packetBuf.readBoolean();
        this.sneak = packetBuf.readBoolean();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer packetBuf) throws IOException
    {
        packetBuf.writeFloat(this.movingStrafing);
        packetBuf.writeFloat(this.moveForward);
        packetBuf.writeBoolean(this.jump);
        packetBuf.writeBoolean(this.sneak);
    }

    public void processPacket(INetHandlerPlayServer netHandler)
    {
        netHandler.processInput(this);
    }

    public float getStrafing()
    {
        return this.movingStrafing;
    }

    public float getForward()
    {
        return this.moveForward;
    }

    public boolean getJump()
    {
        return this.jump;
    }

    public boolean getSneak()
    {
        return this.sneak;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayServer)p_148833_1_);
    }
}
