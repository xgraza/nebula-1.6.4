package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C16PacketClientStatus extends Packet
{
    private EnumState field_149437_a;
    private static final String __OBFID = "CL_00001348";

    public C16PacketClientStatus() {}

    public C16PacketClientStatus(EnumState p_i45242_1_)
    {
        this.field_149437_a = p_i45242_1_;
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.field_149437_a = EnumState.field_151404_e[p_148837_1_.readByte() % EnumState.field_151404_e.length];
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.field_149437_a.field_151403_d);
    }

    public void processPacket(INetHandlerPlayServer p_149436_1_)
    {
        p_149436_1_.processClientStatus(this);
    }

    public EnumState func_149435_c()
    {
        return this.field_149437_a;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayServer)p_148833_1_);
    }

    public static enum EnumState
    {
        PERFORM_RESPAWN("PERFORM_RESPAWN", 0, 0),
        REQUEST_STATS("REQUEST_STATS", 1, 1),
        OPEN_INVENTORY_ACHIEVEMENT("OPEN_INVENTORY_ACHIEVEMENT", 2, 2);
        private final int field_151403_d;
        private static final EnumState[] field_151404_e = new EnumState[values().length];

        private static final EnumState[] $VALUES = new EnumState[]{PERFORM_RESPAWN, REQUEST_STATS, OPEN_INVENTORY_ACHIEVEMENT};
        private static final String __OBFID = "CL_00001349";

        private EnumState(String p_i45241_1_, int p_i45241_2_, int p_i45241_3_)
        {
            this.field_151403_d = p_i45241_3_;
        }

        static {
            EnumState[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2)
            {
                EnumState var3 = var0[var2];
                field_151404_e[var3.field_151403_d] = var3;
            }
        }
    }
}
