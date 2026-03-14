package net.minecraft.network.play.server;

import net.minecraft.entity.DataWatcher;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayClient;

import java.io.IOException;
import java.util.List;

public class S1CPacketEntityMetadata extends Packet
{
    private int entityId;
    private List<DataWatcher.WatchableObject> changedProperties;

    public S1CPacketEntityMetadata()
    {
    }

    public S1CPacketEntityMetadata(int entityId, DataWatcher dataWatcher, boolean p_i45217_3_)
    {
        this.entityId = entityId;

        if (p_i45217_3_)
        {
            this.changedProperties = dataWatcher.getAllWatched();
        } else
        {
            this.changedProperties = dataWatcher.getChanged();
        }
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer packetBuf) throws IOException
    {
        this.entityId = packetBuf.readInt();
        this.changedProperties = DataWatcher.readWatchedListFromPacketBuffer(packetBuf);
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer packetBuf) throws IOException
    {
        packetBuf.writeInt(this.entityId);
        DataWatcher.writeWatchedListToPacketBuffer(this.changedProperties, packetBuf);
    }

    public void processPacket(INetHandlerPlayClient p_149377_1_)
    {
        p_149377_1_.handleEntityMetadata(this);
    }

    public List<DataWatcher.WatchableObject> getChangedProperties()
    {
        return this.changedProperties;
    }

    public int getEntityId()
    {
        return this.entityId;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayClient) p_148833_1_);
    }
}
