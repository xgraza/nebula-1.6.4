//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica;

import lol.nebula.util.world.WorldUtils;
import net.minecraft.client.*;
import com.github.lunatrius.schematica.world.*;
import com.github.lunatrius.schematica.lib.*;
import net.minecraft.client.entity.*;
import net.minecraft.world.*;
import java.util.*;
import net.minecraft.init.*;
import com.github.lunatrius.schematica.config.*;
import net.minecraft.block.*;
import net.minecraft.util.*;
import net.minecraft.item.*;
import net.minecraft.entity.*;
import net.minecraft.network.*;
import net.minecraft.entity.player.*;
import net.minecraft.network.play.client.*;
import net.minecraft.client.network.*;

public class SchematicPrinter
{
    public static final SchematicPrinter INSTANCE;
    private final Minecraft minecraft;
    private final Settings settings;
    private boolean isEnabled;
    private boolean isPrinting;
    private int hasSwitchedItems;
    private Random rand;
    private SchematicWorld schematic;
    private byte[][][] timeout;
    
    public SchematicPrinter() {
        this.minecraft = Minecraft.getMinecraft();
        this.settings = Settings.instance;
        this.hasSwitchedItems = 0;
        this.rand = new Random();
        this.schematic = null;
        this.timeout = null;
    }
    
    public boolean isEnabled() {
        return this.isEnabled;
    }
    
    public void setEnabled(final boolean isEnabled) {
        this.isEnabled = isEnabled;
    }
    
    public boolean togglePrinting() {
        return this.isPrinting = !this.isPrinting;
    }
    
    public boolean isPrinting() {
        return this.isPrinting;
    }
    
    public void setPrinting(final boolean isPrinting) {
        this.isPrinting = isPrinting;
    }
    
    public SchematicWorld getSchematic() {
        return this.schematic;
    }
    
    public void setSchematic(final SchematicWorld schematic) {
        this.isPrinting = false;
        this.schematic = schematic;
        this.refresh();
    }
    
    public void refresh() {
        if (this.schematic != null) {
            this.timeout = new byte[this.schematic.getWidth()][this.schematic.getHeight()][this.schematic.getLength()];
        }
        else {
            this.timeout = null;
        }
    }
    
    public boolean print() {
        if (this.hasSwitchedItems > 0) {
            --this.hasSwitchedItems;
        }
        final EntityClientPlayerMP player = this.minecraft.thePlayer;
        final World world = (World)this.minecraft.theWorld;
        this.syncSneaking(player, true);
        final int minX = Math.max(0, (int)this.settings.getTranslationX() - 5);
        final int maxX = Math.min(this.schematic.getWidth(), (int)this.settings.getTranslationX() + 5);
        final int minY = Math.max(0, (int)this.settings.getTranslationY() - 5);
        final int maxY = Math.min(this.schematic.getHeight(), (int)this.settings.getTranslationY() + 5);
        final int minZ = Math.max(0, (int)this.settings.getTranslationZ() - 5);
        final int maxZ = Math.min(this.schematic.getLength(), (int)this.settings.getTranslationZ() + 5);
        final int slot = player.inventory.currentItem;
        final boolean isSneaking = player.isSneaking();
        final int renderingLayer = this.schematic.getRenderingLayer();
        for (int y = minY; y < maxY; ++y) {
            if (renderingLayer < 0 || y == renderingLayer) {
                for (int x = minX; x < maxX; ++x) {
                    for (int z = minZ; z < maxZ; ++z) {
                        final Block block = this.schematic.getBlock(x, y, z);
                        if (block != Blocks.air) {
                            if (this.timeout[x][y][z] > 0) {
                                final byte[] array = this.timeout[x][y];
                                final int n = z;
                                array[n] -= (byte)Reference.config.placeDelay;
                            }
                            else {
                                final int wx = (int)this.settings.offset.x + x;
                                final int wy = (int)this.settings.offset.y + y;
                                final int wz = (int)this.settings.offset.z + z;
                                final double distx = player.posX - (wx + 0.5);
                                final double disty = player.posY - (wy + 0.5);
                                final double distz = player.posZ - (wz + 0.5);
                                if (distx * distx + disty * disty + distz * distz < 25.0) {
                                    final Block realBlock = world.getBlock(wx, wy, wz);
                                    if (world.isAirBlock(wx, wy, wz) || realBlock == null || realBlock.canPlaceBlockAt(world, wx, wy, wz)) {
                                        final int metadata = this.schematic.getBlockMetadata(x, y, z);
                                        if (this.placeBlock(this.minecraft, world, (EntityPlayer)player, wx, wy, wz, BlockInfo.getItemFromBlock(block), metadata)) {
                                            this.timeout[x][y][z] = (byte)Reference.config.timeout;
                                            if (!Reference.config.placeInstantly) {
                                                player.inventory.currentItem = slot;
                                                this.syncSneaking(player, isSneaking);
                                                return true;
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        player.inventory.currentItem = slot;
        this.syncSneaking(player, isSneaking);
        return true;
    }
    
    private boolean isSolid(final World world, int x, int y, int z, final EnumFacing side) {
        x += side.getFrontOffsetX();
        y += side.getFrontOffsetY();
        z += side.getFrontOffsetZ();
        final Block block = world.getBlock(x, y, z);
        return block != null && block != Blocks.air /*!block.isAir((IBlockAccess)world, x, y, z)*/ && !(block instanceof BlockLiquid) && block.getMaterial().isReplaceable(); /*!block.isReplaceable((IBlockAccess)world, x, y, z);*/
    }
    
    private EnumFacing[] getSolidSides(final World world, final int x, final int y, final int z) {
        final List<EnumFacing> list = new ArrayList<EnumFacing>();
        for (final EnumFacing side : EnumFacing.values()) {
            if (this.isSolid(world, x, y, z, side)) {
                list.add(side);
            }
        }
        final EnumFacing[] sides = new EnumFacing[list.size()];
        return list.toArray(sides);
    }
    
    private boolean placeBlock(final Minecraft minecraft, final World world, final EntityPlayer player, final int x, final int y, final int z, final Item item, final int itemDamage) {
        if (item instanceof ItemBucket || item == Items.sign) {
            return false;
        }
        final PlacementData data = BlockInfo.getPlacementDataFromItem(item);
        if (!this.isValidOrientation(player, x, y, z, data, itemDamage)) {
            return false;
        }
        final EnumFacing[] solidSides = this.getSolidSides(world, x, y, z);
        EnumFacing direction = null;
        float offsetY = 0.0f;
        if (solidSides.length > 0) {
            if (data != null) {
                final EnumFacing[] validDirections = data.getValidDirections(solidSides, itemDamage);
                if (validDirections.length > 0) {
                    direction = validDirections[0];
                }
                offsetY = data.getOffsetFromMetadata(itemDamage);
                if (data.maskMetaInHand != -1) {
                    if (!this.swapToItem(player.inventory, item, data.getMetaInHand(itemDamage))) {
                        return false;
                    }
                }
                else if (!this.swapToItem(player.inventory, item)) {
                    return false;
                }
            }
            else {
                direction = solidSides[0];
                if (Block.getBlockFromItem(item) instanceof BlockFalling) {
                    for (final EnumFacing dir : solidSides) {
                        if (dir == EnumFacing.DOWN) {
                            direction = EnumFacing.DOWN;
                        }
                    }
                    if (direction != EnumFacing.DOWN) {
                        return false;
                    }
                }
                if (!this.swapToItem(player.inventory, item)) {
                    return false;
                }
            }
        }
        return (direction != null || !Reference.config.placeAdjacent) && this.placeBlock(minecraft, world, player, x, y, z, direction, 0.0f, offsetY, 0.0f);
    }
    
    private boolean isValidOrientation(final EntityPlayer player, final int x, final int y, final int z, final PlacementData data, final int metadata) {
        final EnumFacing orientation = this.settings.orientation;
        if (data != null) {
            switch (data.type) {
                case BLOCK: {
                    return true;
                }
                case PLAYER: {
                    final Integer integer = data.mapping.get(orientation);
                    if (integer != null) {
                        return integer == (metadata & data.maskMeta);
                    }
                    break;
                }
                case PISTON: {
                    final Integer integer = data.mapping.get(orientation);
                    if (integer != null) {
                        return BlockPistonBase.func_150071_a((World)null, x, y, z, (EntityLivingBase)player) == BlockPistonBase.func_150076_b(metadata);
                    }
                    break;
                }
            }
            return false;
        }
        return true;
    }
    
    private boolean placeBlock(final Minecraft minecraft, final World world, final EntityPlayer player, int x, int y, int z, final EnumFacing direction, final float offsetX, final float offsetY, final float offsetZ) {
        final ItemStack itemStack = player.getCurrentEquippedItem();
        boolean success = false;
        x += direction.getFrontOffsetX();
        y += direction.getFrontOffsetY();
        z += direction.getFrontOffsetZ();
        final int side = WorldUtils.getOpposite(direction).ordinal(); //direction.getOpposite().ordinal();
        //success = !ForgeEventFactory.onPlayerInteract((EntityPlayer)minecraft.thePlayer, PlayerInteractEvent.Action.RIGHT_CLICK_BLOCK, x, y, z, side).isCanceled();
        success = minecraft.playerController.onPlayerRightClick(minecraft.thePlayer, minecraft.theWorld, itemStack, x, y, z, side, Vec3.createVectorHelper((double)(x + offsetX), (double)(y + offsetY), (double)(z + offsetZ)));
        if (success) {
            minecraft.thePlayer.swingItem();
//            success = minecraft.playerController.onPlayerRightClick(player, world, itemStack, x, y, z, side, Vec3.createVectorHelper((double)(x + offsetX), (double)(y + offsetY), (double)(z + offsetZ)));
//            if (success) {
//                minecraft.thePlayer.swingItem();
//            }
        }
        if (itemStack != null && itemStack.stackSize == 0 && success) {
            player.inventory.mainInventory[player.inventory.currentItem] = null;
        }
        return success;
    }
    
    private void syncSneaking(final EntityClientPlayerMP player, final boolean isSneaking) {
        player.setSneaking(isSneaking);
        player.sendQueue.addToSendQueue((Packet)new C0BPacketEntityAction((Entity)player, isSneaking ? 1 : 2));
    }
    
    private boolean swapToItem(final InventoryPlayer inventory, final Item item, final int itemDamage) {
        final int slot = this.getInventorySlotWithItem(inventory, item, itemDamage);
        if (slot > -1 && slot < 9) {
            inventory.currentItem = slot;
            dispatchPacketInstantly((Packet)new C09PacketHeldItemChange(inventory.currentItem));
            return true;
        }
        if (slot >= 9 && slot < 36 && this.hasSwitchedItems == 0) {
            int hotbar = -1;
            for (int count = 0; count < 9; ++count) {
                if (this.minecraft.thePlayer.inventory.mainInventory[count] == null) {
                    hotbar = count;
                }
            }
            if (hotbar == -1) {
                hotbar = this.rand.nextInt(9);
            }
            inventory.currentItem = hotbar;
            dispatchPacketInstantly((Packet)new C09PacketHeldItemChange(inventory.currentItem));
            dispatchPacketInstantly((Packet)new C0EPacketClickWindow(this.minecraft.thePlayer.inventoryContainer.windowId, slot, hotbar, 2, (ItemStack)null, this.minecraft.thePlayer.inventoryContainer.getNextTransactionID(this.minecraft.thePlayer.inventory)));
            dispatchPacketInstantly((Packet)new C0EPacketClickWindow(this.minecraft.thePlayer.inventoryContainer.windowId, 0, 0, 0, new ItemStack(Items.record_stal, 1), this.minecraft.thePlayer.inventoryContainer.getNextTransactionID(this.minecraft.thePlayer.inventory)));
            this.hasSwitchedItems = 20;
            return true;
        }
        return false;
    }
    
    private static void dispatchPacketInstantly(final Packet packet) {
        final NetHandlerPlayClient netHandlerPlayClient = Minecraft.getMinecraft().getNetHandler();
        if (netHandlerPlayClient != null) {
            netHandlerPlayClient.getNetworkManager().channel.writeAndFlush((Object)packet);
        }
    }
    
    private boolean swapToItem(final InventoryPlayer inventory, final Item item) {
        final int slot = this.getInventorySlotWithItem(inventory, item);
        if (slot > -1 && slot < 9) {
            inventory.currentItem = slot;
            dispatchPacketInstantly((Packet)new C09PacketHeldItemChange(inventory.currentItem));
            return true;
        }
        if (slot >= 9 && slot < 36 && this.hasSwitchedItems == 0) {
            int hotbar = -1;
            for (int count = 0; count < 9; ++count) {
                if (this.minecraft.thePlayer.inventory.mainInventory[count] == null) {
                    hotbar = count;
                }
            }
            if (hotbar == -1) {
                hotbar = this.rand.nextInt(9);
            }
            inventory.currentItem = hotbar;
            dispatchPacketInstantly((Packet)new C09PacketHeldItemChange(inventory.currentItem));
            dispatchPacketInstantly((Packet)new C0EPacketClickWindow(this.minecraft.thePlayer.inventoryContainer.windowId, slot, hotbar, 2, (ItemStack)null, this.minecraft.thePlayer.inventoryContainer.getNextTransactionID(this.minecraft.thePlayer.inventory)));
            dispatchPacketInstantly((Packet)new C0EPacketClickWindow(this.minecraft.thePlayer.inventoryContainer.windowId, 0, 0, 0, new ItemStack(Items.record_stal, 1), this.minecraft.thePlayer.inventoryContainer.getNextTransactionID(this.minecraft.thePlayer.inventory)));
            this.hasSwitchedItems = 20;
            return true;
        }
        return false;
    }
    
    private int getInventorySlotWithItem(final InventoryPlayer inventory, final Item item, final int itemDamage) {
        for (int i = 0; i < inventory.mainInventory.length; ++i) {
            if (inventory.mainInventory[i] != null && inventory.mainInventory[i].getItem() == item && inventory.mainInventory[i].getItemDamage() == itemDamage) {
                return i;
            }
        }
        return -1;
    }
    
    private int getInventorySlotWithItem(final InventoryPlayer inventory, final Item item) {
        for (int i = 0; i < inventory.mainInventory.length; ++i) {
            if (inventory.mainInventory[i] != null && inventory.mainInventory[i].getItem() == item && inventory.mainInventory[i].getItemDamage() == 0) {
                return i;
            }
        }
        return -1;
    }
    
    static {
        INSTANCE = new SchematicPrinter();
    }
}
