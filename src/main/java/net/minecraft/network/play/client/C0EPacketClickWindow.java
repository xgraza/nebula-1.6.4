package net.minecraft.network.play.client;

import java.io.IOException;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.network.INetHandler;
import net.minecraft.network.Packet;
import net.minecraft.network.PacketBuffer;
import net.minecraft.network.play.INetHandlerPlayServer;

public class C0EPacketClickWindow extends Packet
{
    private int windowId;
    private int slot;
    private int button;
    private short field_149550_d;
    private ItemStack itemStack;
    private int type;
    private static final String __OBFID = "CL_00001353";

    public C0EPacketClickWindow() {}

    public C0EPacketClickWindow(int windowId, int slot, int button, int type, ItemStack itemStack, short p_i45246_6_)
    {
        this.windowId = windowId;
        this.slot = slot;
        this.button = button;
        this.itemStack = itemStack != null ? itemStack.copy() : null;
        this.field_149550_d = p_i45246_6_;
        this.type = type;
    }

    public void processPacket(INetHandlerPlayServer p_149545_1_)
    {
        p_149545_1_.processClickWindow(this);
    }

    /**
     * Reads the raw packet data from the data stream.
     */
    public void readPacketData(PacketBuffer p_148837_1_) throws IOException
    {
        this.windowId = p_148837_1_.readByte();
        this.slot = p_148837_1_.readShort();
        this.button = p_148837_1_.readByte();
        this.field_149550_d = p_148837_1_.readShort();
        this.type = p_148837_1_.readByte();
        this.itemStack = p_148837_1_.readItemStackFromBuffer();
    }

    /**
     * Writes the raw packet data to the data stream.
     */
    public void writePacketData(PacketBuffer p_148840_1_) throws IOException
    {
        p_148840_1_.writeByte(this.windowId);
        p_148840_1_.writeShort(this.slot);
        p_148840_1_.writeByte(this.button);
        p_148840_1_.writeShort(this.field_149550_d);
        p_148840_1_.writeByte(this.type);
        p_148840_1_.writeItemStackToBuffer(this.itemStack);
    }

    /**
     * Returns a string formatted as comma separated [field]=[value] values. Used by Minecraft for logging purposes.
     */
    public String serialize()
    {
        return this.itemStack != null ? String.format("id=%d, slot=%d, button=%d, type=%d, itemid=%d, itemcount=%d, itemaux=%d", new Object[] {Integer.valueOf(this.windowId), Integer.valueOf(this.slot), Integer.valueOf(this.button), Integer.valueOf(this.type), Integer.valueOf(Item.getIdFromItem(this.itemStack.getItem())), Integer.valueOf(this.itemStack.stackSize), Integer.valueOf(this.itemStack.getItemDamage())}): String.format("id=%d, slot=%d, button=%d, type=%d, itemid=-1", new Object[] {Integer.valueOf(this.windowId), Integer.valueOf(this.slot), Integer.valueOf(this.button), Integer.valueOf(this.type)});
    }

    public int getWindowId()
    {
        return this.windowId;
    }

    public int getSlot()
    {
        return this.slot;
    }

    public int getButton()
    {
        return this.button;
    }

    public short func_149547_f()
    {
        return this.field_149550_d;
    }

    public ItemStack getItemStack()
    {
        return this.itemStack;
    }

    public int getType()
    {
        return this.type;
    }

    public void processPacket(INetHandler p_148833_1_)
    {
        this.processPacket((INetHandlerPlayServer)p_148833_1_);
    }
}
