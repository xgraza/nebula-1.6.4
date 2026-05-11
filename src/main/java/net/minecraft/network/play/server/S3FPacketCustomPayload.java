package net.minecraft.network.play.server;

import io.netty.buffer.ByteBuf;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;

public class S3FPacketCustomPayload extends Packet
{
    private String channel;
    private byte[] payload;

    public S3FPacketCustomPayload()
    {
    }

    public S3FPacketCustomPayload(String channel, ByteBuf payload)
    {
        this(channel, payload.array());
    }

    public S3FPacketCustomPayload(String channel, byte[] payload)
    {
        this.channel = channel;
        this.payload = payload;

        if (payload.length >= Short.MAX_VALUE)
        {
            throw new IllegalArgumentException("Payload may not be larger than 32767 bytes");
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer buffer) throws IOException
    {
        this.channel = buffer.readStringFromBuffer(20);
        this.payload = new byte[buffer.readUnsignedShort()];
        buffer.readBytes(this.payload);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer buffer) throws IOException
    {
        buffer.writeStringToBuffer(this.channel);
        buffer.writeShort(this.payload.length);
        buffer.writeBytes(this.payload);
    }

    public void processPacket(INetHandlerPlayClient netHandlerClient)
    {
        netHandlerClient.handleCustomPayload(this);
    }

    public void processPacket(INetHandler netHandler)
    {
        this.processPacket((INetHandlerPlayClient) netHandler);
    }

    public String getChannel()
    {
        return this.channel;
    }

    public byte[] getPayload()
    {
        return this.payload;
    }
}
