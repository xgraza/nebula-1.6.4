//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.world;

import net.minecraft.world.storage.*;
import net.minecraft.profiler.*;
import com.github.lunatrius.schematica.lib.*;
import net.minecraft.item.*;
import net.minecraft.nbt.*;
import java.io.*;
import com.github.lunatrius.schematica.config.*;
import net.minecraft.block.*;
import net.minecraft.init.*;
import java.util.*;
import net.minecraft.world.biome.*;
import net.minecraft.world.chunk.*;
import net.minecraft.client.multiplayer.*;
import net.minecraft.entity.*;
import net.minecraft.util.*;
import net.minecraft.tileentity.*;
import com.github.lunatrius.core.util.vector.*;
import net.minecraft.world.*;

public class SchematicWorld extends World
{
    private static final WorldSettings WORLD_SETTINGS;
    private static final Comparator<ItemStack> BLOCK_COMPARATOR;
    public static final ItemStack DEFAULT_ICON;
    private ItemStack icon;
    private short[][][] blocks;
    private byte[][][] metadata;
    private final List<TileEntity> tileEntities;
    private final List<ItemStack> blockList;
    private short width;
    private short length;
    private short height;
    private boolean isRendering;
    private int renderingLayer;
    
    public SchematicWorld() {
        super((ISaveHandler)new SaveHandlerMP(), "Schematica", (WorldProvider)null, SchematicWorld.WORLD_SETTINGS, (Profiler)null);
        this.tileEntities = new ArrayList<TileEntity>();
        this.blockList = new ArrayList<ItemStack>();
        this.icon = SchematicWorld.DEFAULT_ICON.copy();
        this.blocks = null;
        this.metadata = null;
        this.tileEntities.clear();
        this.width = 0;
        this.length = 0;
        this.height = 0;
        this.isRendering = false;
        this.renderingLayer = -1;
    }
    
    public SchematicWorld(final ItemStack icon, final short[][][] blocks, final byte[][][] metadata, final List<TileEntity> tileEntities, final short width, final short height, final short length) {
        super((ISaveHandler)new SaveHandlerMP(), "Schematica", WorldProvider.getProviderForDimension(0), SchematicWorld.WORLD_SETTINGS, (Profiler)null);
        this.tileEntities = new ArrayList<TileEntity>();
        this.blockList = new ArrayList<ItemStack>();
        this.icon = SchematicWorld.DEFAULT_ICON.copy();
        this.blocks = null;
        this.metadata = null;
        this.tileEntities.clear();
        this.width = 0;
        this.length = 0;
        this.height = 0;
        this.isRendering = false;
        this.renderingLayer = -1;
        this.icon = ((icon != null) ? icon : SchematicWorld.DEFAULT_ICON.copy());
        this.blocks = blocks.clone();
        this.metadata = metadata.clone();
        if (tileEntities != null) {
            this.tileEntities.addAll(tileEntities);
            for (final TileEntity tileEntity : this.tileEntities) {
                tileEntity.setWorldObj((World)this);
                try {
                    tileEntity.validate();
                }
                catch (Exception e) {
                    Reference.logger.error(String.format("TileEntity validation for %s failed!", tileEntity.getClass()), (Throwable)e);
                }
            }
        }
        this.width = width;
        this.length = length;
        this.height = height;
        this.generateBlockList();
    }
    
    public SchematicWorld(final String iconName, final short[][][] blocks, final byte[][][] metadata, final List<TileEntity> tileEntities, final short width, final short height, final short length) {
        this(getIconFromName(iconName), blocks, metadata, tileEntities, width, height, length);
    }
    
    public static ItemStack getIconFromName(final String iconName) {
        String name = "";
        int damage = 0;
        final String[] parts = iconName.split(",");
        if (parts.length >= 1) {
            name = parts[0];
            if (parts.length >= 2) {
                try {
                    damage = Integer.parseInt(parts[1]);
                }
                catch (NumberFormatException ex) {}
            }
        }
        //ItemStack icon = new ItemStack((Block)GameData.getBlockRegistry().getObject(name), 1, damage);
        ItemStack icon = new ItemStack((Block)Block.blockRegistry.getObject(name), 1, damage);
        if (icon.getItem() != null) {
            return icon;
        }
        //icon = new ItemStack((Item)GameData.getItemRegistry().getObject(name), 1, damage);
        icon = new ItemStack((Item)Item.itemRegistry.getObject(name), 1, damage);
        if (icon.getItem() != null) {
            return icon;
        }
        return SchematicWorld.DEFAULT_ICON.copy();
    }
    
    public static ItemStack getIconFromNBT(final NBTTagCompound tagCompound) {
        ItemStack icon = SchematicWorld.DEFAULT_ICON.copy();
        if (tagCompound != null && tagCompound.hasKey("Icon")) {
            icon.readFromNBT(tagCompound.getCompoundTag("Icon"));
            if (icon.getItem() == null) {
                icon = SchematicWorld.DEFAULT_ICON.copy();
            }
        }
        return icon;
    }
    
    public static ItemStack getIconFromFile(final File file) {
        try {
            final InputStream stream = new FileInputStream(file);
            final NBTTagCompound tagCompound = CompressedStreamTools.readCompressed(stream);
            return getIconFromNBT(tagCompound);
        }
        catch (Exception e) {
            Reference.logger.error("Failed to read schematic icon!", (Throwable)e);
            return SchematicWorld.DEFAULT_ICON.copy();
        }
    }
    
    private void generateBlockList() {
        this.blockList.clear();
        for (int y = 0; y < this.height; ++y) {
            for (int z = 0; z < this.length; ++z) {
                for (int x = 0; x < this.width; ++x) {
                    Block block = this.getBlock(x, y, z);
                    Item item = Item.getItemFromBlock(block);
                    int itemDamage = this.metadata[x][y][z];
                    if (block != null) {
                        if (block != Blocks.air) {
                            if (!BlockInfo.BLOCK_LIST_IGNORE_BLOCK.contains(block)) {
                                if (BlockInfo.BLOCK_LIST_IGNORE_METADATA.contains(block)) {
                                    itemDamage = 0;
                                }
                                if (BlockInfo.BLOCK_ITEM_MAP.containsKey(block)) {
                                    item = BlockInfo.BLOCK_ITEM_MAP.get(block);
                                    final Block blockFromItem = Block.getBlockFromItem(item);
                                    if (blockFromItem != Blocks.air) {
                                        block = blockFromItem;
                                    }
                                    else {
                                        itemDamage = 0;
                                    }
                                }
                                if (block instanceof BlockLog || block instanceof BlockLeavesBase) {
                                    itemDamage &= 0x3;
                                }
                                if (block instanceof BlockSlab) {
                                    itemDamage &= 0x7;
                                }
                                if (!(block instanceof BlockDoublePlant) || (itemDamage & 0x8) != 0x8) {
                                    if (block == Blocks.cocoa) {
                                        itemDamage = 3;
                                    }
                                    if (item == Items.skull) {
                                        final TileEntity tileEntity = this.getTileEntity(x, y, z);
                                        if (tileEntity instanceof TileEntitySkull) {
                                            itemDamage = ((TileEntitySkull)tileEntity).func_145904_a();
                                        }
                                    }
                                    ItemStack itemStack = null;
                                    for (final ItemStack stack : this.blockList) {
                                        if (stack.getItem() == item && stack.getItemDamage() == itemDamage) {
                                            final ItemStack itemStack2;
                                            itemStack = (itemStack2 = stack);
                                            ++itemStack2.stackSize;
                                            break;
                                        }
                                    }
                                    if (itemStack == null) {
                                        itemStack = new ItemStack(item, 1, itemDamage);
                                        if (itemStack.getItem() != null) {
                                            this.blockList.add(itemStack);
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
        Collections.sort(this.blockList, SchematicWorld.BLOCK_COMPARATOR);
    }
    
    public int getBlockIdRaw(final int x, final int y, final int z) {
        if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.height || z >= this.length) {
            return 0;
        }
        return this.blocks[x][y][z];
    }
    
    private int getBlockId(final int x, final int y, final int z) {
        if (this.getRenderingLayer() != -1 && this.getRenderingLayer() != y) {
            return 0;
        }
        return this.getBlockIdRaw(x, y, z);
    }
    
    public Block getBlockRaw(final int x, final int y, final int z) {
        return (Block) Block.blockRegistry.getObjectForID(getBlockIdRaw(x, y, z));
        //return (Block)GameData.getBlockRegistry().getObjectForID(this.getBlockIdRaw(x, y, z));
    }
    
    public Block getBlock(final int x, final int y, final int z) {
        return (Block) Block.blockRegistry.getObjectForID(getBlockId(x, y, z));
        //return (Block)GameData.getBlockRegistry().getObjectForID(this.getBlockId(x, y, z));
    }
    
    public TileEntity getTileEntity(final int x, final int y, final int z) {
        for (final TileEntity tileEntity : this.tileEntities) {
            if (tileEntity.xCoord == x && tileEntity.yCoord == y && tileEntity.zCoord == z) {
                return tileEntity;
            }
        }
        return null;
    }
    
    public int getSkyBlockTypeBrightness(final EnumSkyBlock skyBlock, final int x, final int y, final int z) {
        return 15;
    }
    
    public float getLightBrightness(final int x, final int y, final int z) {
        return 1.0f;
    }
    
    public int getBlockMetadata(final int x, final int y, final int z) {
        if (x < 0 || y < 0 || z < 0 || x >= this.width || y >= this.height || z >= this.length) {
            return 0;
        }
        return this.metadata[x][y][z];
    }
    
    public boolean isBlockNormalCubeDefault(final int x, final int y, final int z, final boolean _default) {
        final Block block = this.getBlock(x, y, z);
        return block != null && (block.isNormalCube() || _default);
    }
    
    public boolean isAirBlock(final int x, final int y, final int z) {
        final Block block = this.getBlock(x, y, z);
        return block == null || block == Blocks.air; //block.isAir((IBlockAccess)this, x, y, z);
    }
    
    public BiomeGenBase getBiomeGenForCoords(final int x, final int z) {
        return BiomeGenBase.jungle;
    }
    
    public int getWidth() {
        return this.width;
    }
    
    public int getLength() {
        return this.length;
    }
    
    public int getHeight() {
        return this.height;
    }
    
    public boolean extendedLevelsInChunkCache() {
        return false;
    }
    
    protected IChunkProvider createChunkProvider() {
        return (IChunkProvider)new ChunkProviderClient((World)this);
    }
    
    public Entity getEntityByID(final int id) {
        return null;
    }
    
    public boolean blockExists(final int x, final int y, final int z) {
        return false;
    }
    
    public boolean setBlockMetadata(final int x, final int y, final int z, final int metadata, final int flag) {
        this.metadata[x][y][z] = (byte)(metadata & 0xFF);
        return true;
    }
    
    public boolean isSideSolid(final int x, final int y, final int z, final EnumFacing side) {
        return this.isSideSolid(x, y, z, side, false);
    }
    
    public boolean isSideSolid(final int x, final int y, final int z, final EnumFacing side, final boolean _default) {
        final Block block = this.getBlock(x, y, z);
        return block != null &&  block.isBlockSolid(this, x, y, z, side.getOrder_a()); //block.isSideSolid((IBlockAccess)this, x, y, z, side);
    }
    
    public ItemStack getIcon() {
        return this.icon;
    }
    
    public void setTileEntities(final List<TileEntity> tileEntities) {
        this.tileEntities.clear();
        this.tileEntities.addAll(tileEntities);
        for (final TileEntity tileEntity : this.tileEntities) {
            tileEntity.setWorldObj((World)this);
            try {
                tileEntity.validate();
            }
            catch (Exception e) {
                Reference.logger.error(String.format("TileEntity validation for %s failed!", tileEntity.getClass()), (Throwable)e);
            }
        }
    }
    
    public List<TileEntity> getTileEntities() {
        return this.tileEntities;
    }
    
    public List<ItemStack> getBlockList() {
        return this.blockList;
    }
    
    public boolean toggleRendering() {
        return this.isRendering = !this.isRendering;
    }
    
    public boolean isRendering() {
        return this.isRendering;
    }
    
    public void setRendering(final boolean isRendering) {
        this.isRendering = isRendering;
    }
    
    public int getRenderingLayer() {
        return this.renderingLayer;
    }
    
    public void setRenderingLayer(final int renderingLayer) {
        this.renderingLayer = renderingLayer;
    }
    
    public void decrementRenderingLayer() {
        this.renderingLayer = MathHelper.clamp_int(this.renderingLayer - 1, -1, this.getHeight() - 1);
    }
    
    public void incrementRenderingLayer() {
        this.renderingLayer = MathHelper.clamp_int(this.renderingLayer + 1, -1, this.getHeight() - 1);
    }
    
    public void refreshChests() {
        for (final TileEntity tileEntity : this.tileEntities) {
            if (tileEntity instanceof TileEntityChest) {
                final TileEntityChest tileEntityChest = (TileEntityChest)tileEntity;
                tileEntityChest.field_145984_a = false;
                tileEntityChest.func_145979_i();
            }
        }
    }
    
    public void flip() {
    }
    
    public void rotate() {
        final short[][][] localBlocks = new short[this.length][this.height][this.width];
        final byte[][][] localMetadata = new byte[this.length][this.height][this.width];
        for (int y = 0; y < this.height; ++y) {
            for (int z = 0; z < this.length; ++z) {
                for (int x = 0; x < this.width; ++x) {
                    // TODO
                    //this.getBlock(x, y, this.length - 1 - z).rotateBlock((World)this, x, y, this.length - 1 - z, EnumFacing.UP);
                    localBlocks[z][y][x] = this.blocks[x][y][this.length - 1 - z];
                    localMetadata[z][y][x] = this.metadata[x][y][this.length - 1 - z];
                }
            }
        }
        this.blocks = localBlocks;
        this.metadata = localMetadata;
        for (final TileEntity tileEntity : this.tileEntities) {
            final int coord = tileEntity.zCoord;
            tileEntity.zCoord = tileEntity.xCoord;
            tileEntity.xCoord = this.length - 1 - coord;
            tileEntity.blockMetadata = this.metadata[tileEntity.xCoord][tileEntity.yCoord][tileEntity.zCoord];
            if (tileEntity instanceof TileEntitySkull && tileEntity.blockMetadata == 1) {
                final TileEntitySkull skullTileEntity = (TileEntitySkull)tileEntity;
                skullTileEntity.func_145903_a(skullTileEntity.func_145906_b() + 12 & 0xF);
            }
        }
        final short tmp = this.width;
        this.width = this.length;
        this.length = tmp;
        this.refreshChests();
    }
    
    public Vector3f dimensions() {
        return new Vector3f((float)this.width, (float)this.height, (float)this.length);
    }
    
    static {
        WORLD_SETTINGS = new WorldSettings(0L, WorldSettings.GameType.CREATIVE, false, false, WorldType.FLAT);
        BLOCK_COMPARATOR = new Comparator<ItemStack>() {
            @Override
            public int compare(final ItemStack itemStackA, final ItemStack itemStackB) {
                return itemStackA.getUnlocalizedName().compareTo(itemStackB.getUnlocalizedName());
            }
        };
        DEFAULT_ICON = new ItemStack((Block)Blocks.grass);
    }
}
