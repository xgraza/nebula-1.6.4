//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.world.schematic;

import com.github.lunatrius.schematica.world.*;
import net.minecraft.tileentity.*;
import net.minecraft.item.*;
import java.util.*;
import net.minecraft.nbt.*;
import com.github.lunatrius.schematica.lib.*;
import net.minecraft.init.*;
import net.minecraft.block.*;

public class SchematicAlpha extends SchematicFormat
{
    public static final String ICON = "Icon";
    public static final String BLOCKS = "Blocks";
    public static final String DATA = "Data";
    public static final String ADD_BLOCKS = "AddBlocks";
    public static final String ADD_BLOCKS_SCHEMATICA = "Add";
    public static final String WIDTH = "Width";
    public static final String LENGTH = "Length";
    public static final String HEIGHT = "Height";
    public static final String MAPPING = "...";
    public static final String MAPPING_SCHEMATICA = "SchematicaMapping";
    public static final String TILE_ENTITIES = "TileEntities";
    public static final String ENTITIES = "Entities";
    
    @Override
    public SchematicWorld readFromNBT(final NBTTagCompound tagCompound) {
        final ItemStack icon = SchematicWorld.getIconFromNBT(tagCompound);
        final byte[] localBlocks = tagCompound.getByteArray("Blocks");
        final byte[] localMetadata = tagCompound.getByteArray("Data");
        boolean extra = false;
        byte[] extraBlocks = null;
        byte[] extraBlocksNibble = null;
        if (tagCompound.hasKey("AddBlocks")) {
            extra = true;
            extraBlocksNibble = tagCompound.getByteArray("AddBlocks");
            extraBlocks = new byte[extraBlocksNibble.length * 2];
            for (int i = 0; i < extraBlocksNibble.length; ++i) {
                extraBlocks[i * 2 + 0] = (byte)(extraBlocksNibble[i] >> 4 & 0xF);
                extraBlocks[i * 2 + 1] = (byte)(extraBlocksNibble[i] & 0xF);
            }
        }
        else if (tagCompound.hasKey("Add")) {
            extra = true;
            extraBlocks = tagCompound.getByteArray("Add");
        }
        final short width = tagCompound.getShort("Width");
        final short length = tagCompound.getShort("Length");
        final short height = tagCompound.getShort("Height");
        final short[][][] blocks = new short[width][height][length];
        final byte[][][] metadata = new byte[width][height][length];
        Short id = null;
        final Map<Short, Short> oldToNew = new HashMap<Short, Short>();
        if (tagCompound.hasKey("SchematicaMapping")) {
            final NBTTagCompound mapping = tagCompound.getCompoundTag("SchematicaMapping");
            final Set<String> names = (Set<String>)mapping.func_150296_c();
            for (final String name : names) {
                //oldToNew.put(mapping.getShort(name), (short)GameData.getBlockRegistry().getId(name));
                oldToNew.put(mapping.getShort(name), (short) Block.getIdFromBlock(Block.getBlockFromName(name)));
            }
        }
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    final int index = x + (y * length + z) * width;
                    blocks[x][y][z] = (short)((localBlocks[index] & 0xFF) | (extra ? ((extraBlocks[index] & 0xFF) << 8) : 0));
                    metadata[x][y][z] = (byte)(localMetadata[index] & 0xFF);
                    if ((id = oldToNew.get(blocks[x][y][z])) != null) {
                        blocks[x][y][z] = id;
                    }
                }
            }
        }
        final List<TileEntity> tileEntities = new ArrayList<TileEntity>();
        final NBTTagList tileEntitiesList = tagCompound.getTagList("TileEntities", 10);
        for (int j = 0; j < tileEntitiesList.tagCount(); ++j) {
            final TileEntity tileEntity = TileEntity.createAndLoadEntity(tileEntitiesList.getCompoundTagAt(j));
            if (tileEntity != null) {
                tileEntities.add(tileEntity);
            }
        }
        return new SchematicWorld(icon, blocks, metadata, tileEntities, width, height, length);
    }
    
    @Override
    public boolean writeToNBT(final NBTTagCompound tagCompound, final SchematicWorld world) {
        final NBTTagCompound tagCompoundIcon = new NBTTagCompound();
        final ItemStack icon = world.getIcon();
        icon.writeToNBT(tagCompoundIcon);
        tagCompound.setTag("Icon", (NBTBase)tagCompoundIcon);
        tagCompound.setShort("Width", (short)world.getWidth());
        tagCompound.setShort("Length", (short)world.getLength());
        tagCompound.setShort("Height", (short)world.getHeight());
        final int size = world.getWidth() * world.getLength() * world.getHeight();
        final byte[] localBlocks = new byte[size];
        final byte[] localMetadata = new byte[size];
        final byte[] extraBlocks = new byte[size];
        final byte[] extraBlocksNibble = new byte[(int)Math.ceil(size / 2.0)];
        boolean extra = false;
        final NBTTagCompound mapping = new NBTTagCompound();
        for (int x = 0; x < world.getWidth(); ++x) {
            for (int y = 0; y < world.getHeight(); ++y) {
                for (int z = 0; z < world.getLength(); ++z) {
                    final int index = x + (y * world.getLength() + z) * world.getWidth();
                    final int blockId = world.getBlockIdRaw(x, y, z);
                    localBlocks[index] = (byte)blockId;
                    localMetadata[index] = (byte)world.getBlockMetadata(x, y, z);
                    if ((extraBlocks[index] = (byte)(blockId >> 8)) > 0) {
                        extra = true;
                    }
                    //final String name = GameData.getBlockRegistry().getNameForObject((Object)world.getBlockRaw(x, y, z));
                    final String name = Block.blockRegistry.getNameForObject(world.getBlockRaw(x, y, z)); //GameData.getBlockRegistry().getNameForObject((Object)world.getBlockRaw(x, y, z));
                    if (!mapping.hasKey(name)) {
                        mapping.setShort(name, (short)blockId);
                    }
                }
            }
        }
        for (int i = 0; i < extraBlocksNibble.length; ++i) {
            if (i * 2 + 1 < extraBlocks.length) {
                extraBlocksNibble[i] = (byte)(extraBlocks[i * 2 + 0] << 4 | extraBlocks[i * 2 + 1]);
            }
            else {
                extraBlocksNibble[i] = (byte)(extraBlocks[i * 2 + 0] << 4);
            }
        }
        int count = 20;
        final NBTTagList tileEntitiesList = new NBTTagList();
        for (final TileEntity tileEntity : world.getTileEntities()) {
            final NBTTagCompound tileEntityTagCompound = new NBTTagCompound();
            try {
                tileEntity.writeToNBT(tileEntityTagCompound);
                tileEntitiesList.appendTag((NBTBase)tileEntityTagCompound);
            }
            catch (Exception e) {
                final int pos = tileEntity.xCoord + (tileEntity.yCoord * world.getLength() + tileEntity.zCoord) * world.getWidth();
                if (--count > 0) {
                    final Block block = world.getBlockRaw(tileEntity.xCoord, tileEntity.yCoord, tileEntity.zCoord);
                    //Reference.logger.error(String.format("Block %s[%s] with TileEntity %s failed to save! Replacing with bedrock...", block, (block != null) ? GameData.getBlockRegistry().getNameForObject((Object)block) : "?", tileEntity.getClass().getName()), (Throwable)e);
                    Reference.logger.error(String.format("Block %s[%s] with TileEntity %s failed to save! Replacing with bedrock...", block, (block != null) ? Block.blockRegistry.getNameForObject(block) : "?", tileEntity.getClass().getName()), (Throwable)e);
                }
                localBlocks[pos] = (byte) Block.getIdFromBlock(Blocks.bedrock); //(byte)GameData.getBlockRegistry().getId((Object)Blocks.bedrock);
                //localBlocks[pos] = (byte)GameData.getBlockRegistry().getId((Object)Blocks.bedrock);
                extraBlocks[pos] = (localMetadata[pos] = 0);
            }
        }
        tagCompound.setString("Materials", "Alpha");
        tagCompound.setByteArray("Blocks", localBlocks);
        tagCompound.setByteArray("Data", localMetadata);
        if (extra) {
            tagCompound.setByteArray("AddBlocks", extraBlocksNibble);
        }
        tagCompound.setTag("Entities", (NBTBase)new NBTTagList());
        tagCompound.setTag("TileEntities", (NBTBase)tileEntitiesList);
        tagCompound.setTag("SchematicaMapping", (NBTBase)mapping);
        return true;
    }
}
