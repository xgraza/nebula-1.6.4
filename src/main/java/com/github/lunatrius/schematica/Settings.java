//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica;

import com.github.lunatrius.core.util.vector.*;
import net.minecraft.block.Block;
import net.minecraft.client.*;
import com.github.lunatrius.schematica.client.renderer.*;
import net.minecraft.client.renderer.*;
import net.minecraft.util.EnumFacing;
import com.github.lunatrius.schematica.world.*;
import com.github.lunatrius.schematica.lib.*;
import com.github.lunatrius.schematica.world.schematic.*;
import net.minecraft.world.*;
import java.io.*;
import net.minecraft.nbt.*;
import net.minecraft.tileentity.*;
import net.minecraft.init.*;
import java.util.*;

public class Settings
{
    public static final Settings instance;
    private final Vector3f translationVector;
    public Minecraft minecraft;
    public Vector3f playerPosition;
    public final List<RendererSchematicChunk> sortedRendererSchematicChunk;
    public RenderBlocks renderBlocks;
    public Vector3f pointA;
    public Vector3f pointB;
    public Vector3f pointMin;
    public Vector3f pointMax;
    public int rotationRender;
    public EnumFacing orientation;
    public Vector3f offset;
    public boolean isRenderingGuide;
    public int chatLines;
    public boolean isSaveEnabled;
    public boolean isLoadEnabled;
    public boolean isPendingReset;
    public int[] increments;
    
    private Settings() {
        this.translationVector = new Vector3f();
        this.minecraft = Minecraft.getMinecraft();
        this.playerPosition = new Vector3f();
        this.sortedRendererSchematicChunk = new ArrayList<RendererSchematicChunk>();
        this.renderBlocks = null;
        this.pointA = new Vector3f();
        this.pointB = new Vector3f();
        this.pointMin = new Vector3f();
        this.pointMax = new Vector3f();
        this.rotationRender = 0;
        this.orientation = null;
        this.offset = new Vector3f();
        this.isRenderingGuide = false;
        this.chatLines = 0;
        this.isSaveEnabled = true;
        this.isLoadEnabled = true;
        this.isPendingReset = false;
        this.increments = new int[] { 1, 5, 15, 50, 250 };
    }
    
    public void reset() {
        this.chatLines = 0;
        SchematicPrinter.INSTANCE.setEnabled(true);
        this.isSaveEnabled = true;
        this.isLoadEnabled = true;
        this.isRenderingGuide = false;
        Schematica.proxy.setActiveSchematic((SchematicWorld)null);
        this.renderBlocks = null;
        while (this.sortedRendererSchematicChunk.size() > 0) {
            this.sortedRendererSchematicChunk.remove(0).delete();
        }
        SchematicPrinter.INSTANCE.setSchematic((SchematicWorld)null);
    }
    
    public void createRendererSchematicChunk() {
        final SchematicWorld schematic = Schematica.proxy.getActiveSchematic();
        final int width = (schematic.getWidth() - 1) / 16 + 1;
        final int height = (schematic.getHeight() - 1) / 16 + 1;
        final int length = (schematic.getLength() - 1) / 16 + 1;
        while (this.sortedRendererSchematicChunk.size() > 0) {
            this.sortedRendererSchematicChunk.remove(0).delete();
        }
        for (int x = 0; x < width; ++x) {
            for (int y = 0; y < height; ++y) {
                for (int z = 0; z < length; ++z) {
                    this.sortedRendererSchematicChunk.add(new RendererSchematicChunk(schematic, x, y, z));
                }
            }
        }
    }
    
    public boolean loadSchematic(final String filename) {
        try {
            final InputStream stream = new FileInputStream(filename);
            final NBTTagCompound tagCompound = CompressedStreamTools.readCompressed(stream);
            if (tagCompound != null) {
                Reference.logger.info((Object)tagCompound);
                final SchematicWorld schematic = SchematicFormat.readFromFile(new File(filename));
                Schematica.proxy.setActiveSchematic(schematic);
                Reference.logger.info(String.format("Loaded %s [w:%d,h:%d,l:%d]", filename, schematic.getWidth(), schematic.getHeight(), schematic.getLength()));
                this.renderBlocks = new RenderBlocks((IBlockAccess)schematic);
                this.createRendererSchematicChunk();
                schematic.setRendering(true);
                SchematicPrinter.INSTANCE.setSchematic(schematic);
            }
        }
        catch (Exception e) {
            Reference.logger.fatal("Failed to load schematic!", (Throwable)e);
            this.reset();
            return false;
        }
        return true;
    }
    
    public boolean saveSchematic(final File directory, String filename, final Vector3f from, final Vector3f to) {
        try {
            final int minX = (int)Math.min(from.x, to.x);
            final int maxX = (int)Math.max(from.x, to.x);
            final int minY = (int)Math.min(from.y, to.y);
            final int maxY = (int)Math.max(from.y, to.y);
            final int minZ = (int)Math.min(from.z, to.z);
            final int maxZ = (int)Math.max(from.z, to.z);
            final short width = (short)(Math.abs(maxX - minX) + 1);
            final short height = (short)(Math.abs(maxY - minY) + 1);
            final short length = (short)(Math.abs(maxZ - minZ) + 1);
            final short[][][] blocks = new short[width][height][length];
            final byte[][][] metadata = new byte[width][height][length];
            final List<TileEntity> tileEntities = new ArrayList<TileEntity>();
            TileEntity tileEntity = null;
            NBTTagCompound tileEntityNBT = null;
            for (int x = minX; x <= maxX; ++x) {
                for (int y = minY; y <= maxY; ++y) {
                    for (int z = minZ; z <= maxZ; ++z) {
                        //blocks[x - minX][y - minY][z - minZ] = (short)GameData.getBlockRegistry().getId((Object)this.minecraft.theWorld.getBlock(x, y, z));
                        blocks[x - minX][y - minY][z - minZ] = (short) Block.getIdFromBlock(this.minecraft.theWorld.getBlock(x, y, z));
                        metadata[x - minX][y - minY][z - minZ] = (byte)this.minecraft.theWorld.getBlockMetadata(x, y, z);
                        tileEntity = this.minecraft.theWorld.getTileEntity(x, y, z);
                        if (tileEntity != null) {
                            try {
                                tileEntityNBT = new NBTTagCompound();
                                tileEntity.writeToNBT(tileEntityNBT);
                                final TileEntity createAndLoadEntity;
                                tileEntity = (createAndLoadEntity = TileEntity.createAndLoadEntity(tileEntityNBT));
                                //createAndLoadEntity.xCoord -= minX;
                                createAndLoadEntity.xCoord -= minX;
                                final TileEntity tileEntity2 = tileEntity;
                                //tileEntity2.yCoord -= minY;
                                tileEntity2.yCoord -= minY;
                                final TileEntity tileEntity3 = tileEntity;
                                //tileEntity3.zCoord -= minZ;
                                tileEntity3.zCoord -= minZ;
                                tileEntities.add(tileEntity);
                            }
                            catch (Exception e) {
                                Reference.logger.error("Error while trying to save tile entity " + tileEntity + "!", (Throwable)e);
                                //blocks[x - minX][y - minY][z - minZ] = (short)GameData.getBlockRegistry().getId((Object)Blocks.bedrock);
                                blocks[x - minX][y - minY][z - minZ] = (short)Block.getIdFromBlock(Blocks.bedrock);
                                metadata[x - minX][y - minY][z - minZ] = 0;
                            }
                        }
                    }
                }
            }
            String iconName = "";
            try {
                final String[] parts = filename.split(";");
                if (parts.length == 2) {
                    iconName = parts[0];
                    filename = parts[1];
                }
            }
            catch (Exception e2) {
                Reference.logger.error("Failed to parse icon data!", (Throwable)e2);
            }
            final SchematicWorld schematicOut = new SchematicWorld(iconName, blocks, metadata, tileEntities, width, height, length);
            SchematicFormat.writeToFile(directory, filename, schematicOut);
        }
        catch (Exception e3) {
            Reference.logger.error("Failed to save schematic!", (Throwable)e3);
            return false;
        }
        return true;
    }
    
    public Vector3f getTranslationVector() {
        this.translationVector.set(this.playerPosition).sub(this.offset);
        return this.translationVector;
    }
    
    public float getTranslationX() {
        return this.playerPosition.x - this.offset.x;
    }
    
    public float getTranslationY() {
        return this.playerPosition.y - this.offset.y;
    }
    
    public float getTranslationZ() {
        return this.playerPosition.z - this.offset.z;
    }
    
    public void refreshSchematic() {
        for (final RendererSchematicChunk renderer : this.sortedRendererSchematicChunk) {
            renderer.setDirty();
        }
    }
    
    public void updatePoints() {
        this.pointMin.x = Math.min(this.pointA.x, this.pointB.x);
        this.pointMin.y = Math.min(this.pointA.y, this.pointB.y);
        this.pointMin.z = Math.min(this.pointA.z, this.pointB.z);
        this.pointMax.x = Math.max(this.pointA.x, this.pointB.x);
        this.pointMax.y = Math.max(this.pointA.y, this.pointB.y);
        this.pointMax.z = Math.max(this.pointA.z, this.pointB.z);
    }
    
    public void moveHere(final Vector3f point) {
        point.x = (float)(int)Math.floor(this.playerPosition.x);
        point.y = (float)(int)Math.floor(this.playerPosition.y - 1.0f);
        point.z = (float)(int)Math.floor(this.playerPosition.z);
        switch (this.rotationRender) {
            case 0: {
                --point.x;
                ++point.z;
                break;
            }
            case 1: {
                --point.x;
                --point.z;
                break;
            }
            case 2: {
                ++point.x;
                --point.z;
                break;
            }
            case 3: {
                ++point.x;
                ++point.z;
                break;
            }
        }
    }
    
    public void moveHere() {
        this.offset.x = (float)(int)Math.floor(this.playerPosition.x);
        this.offset.y = (float)((int)Math.floor(this.playerPosition.y) - 1);
        this.offset.z = (float)(int)Math.floor(this.playerPosition.z);
        final SchematicWorld schematic = Schematica.proxy.getActiveSchematic();
        if (schematic != null) {
            switch (this.rotationRender) {
                case 0: {
                    final Vector3f offset = this.offset;
                    offset.x -= schematic.getWidth();
                    final Vector3f offset2 = this.offset;
                    ++offset2.z;
                    break;
                }
                case 1: {
                    final Vector3f offset3 = this.offset;
                    offset3.x -= schematic.getWidth();
                    final Vector3f offset4 = this.offset;
                    offset4.z -= schematic.getLength();
                    break;
                }
                case 2: {
                    final Vector3f offset5 = this.offset;
                    ++offset5.x;
                    final Vector3f offset6 = this.offset;
                    offset6.z -= schematic.getLength();
                    break;
                }
                case 3: {
                    final Vector3f offset7 = this.offset;
                    ++offset7.x;
                    final Vector3f offset8 = this.offset;
                    ++offset8.z;
                    break;
                }
            }
        }
    }
    
    static {
        instance = new Settings();
    }
}
