//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.config;

import net.minecraft.util.EnumFacing;
import java.util.*;

public class PlacementData
{
    public static final EnumFacing[] VALID_DIRECTIONS;
    public final PlacementType type;
    public int maskOffset;
    public float offsetLowY;
    public float offsetHighY;
    public int maskMetaInHand;
    public int bitShiftMetaInHand;
    public int maskMeta;
    public final Map<EnumFacing, Integer> mapping;
    
    public PlacementData(final PlacementType type, final int... metadata) {
        this.maskOffset = 0;
        this.offsetLowY = 0.0f;
        this.offsetHighY = 1.0f;
        this.maskMetaInHand = -1;
        this.bitShiftMetaInHand = 0;
        this.maskMeta = 15;
        this.mapping = new HashMap<EnumFacing, Integer>();
        this.type = type;
        for (int i = 0; i < PlacementData.VALID_DIRECTIONS.length && i < metadata.length; ++i) {
            if (metadata[i] >= 0 && metadata[i] <= 15) {
                this.mapping.put(PlacementData.VALID_DIRECTIONS[i], metadata[i]);
            }
        }
    }
    
    public PlacementData setOffset(final int maskOffset, final float offsetLowY, final float offsetHighY) {
        this.maskOffset = maskOffset;
        this.offsetLowY = offsetLowY;
        this.offsetHighY = offsetHighY;
        return this;
    }
    
    public PlacementData setMaskMetaInHand(final int maskMetaInHand) {
        this.maskMetaInHand = maskMetaInHand;
        return this;
    }
    
    public PlacementData setBitShiftMetaInHand(final int bitShiftMetaInHand) {
        this.bitShiftMetaInHand = bitShiftMetaInHand;
        return this;
    }
    
    public PlacementData setMaskMeta(final int maskMeta) {
        this.maskMeta = maskMeta;
        return this;
    }
    
    public float getOffsetFromMetadata(final int metadata) {
        return ((metadata & this.maskOffset) == 0x0) ? this.offsetLowY : this.offsetHighY;
    }
    
    public int getMetaInHand(int metadata) {
        if (this.maskMetaInHand != -1) {
            metadata &= this.maskMetaInHand;
        }
        if (this.bitShiftMetaInHand > 0) {
            metadata >>= this.bitShiftMetaInHand;
        }
        else if (this.bitShiftMetaInHand < 0) {
            metadata <<= -this.bitShiftMetaInHand;
        }
        return metadata;
    }
    
    public EnumFacing[] getValidDirections(final EnumFacing[] solidSides, final int metadata) {
        final List<EnumFacing> list = new ArrayList<EnumFacing>();
        for (final EnumFacing direction : solidSides) {
            Label_0164: {
                if (this.maskOffset != 0) {
                    if ((metadata & this.maskOffset) == 0x0) {
                        if (this.offsetLowY < 0.5f && direction == EnumFacing.UP) {
                            break Label_0164;
                        }
                    }
                    else if (this.offsetLowY < 0.5f && direction == EnumFacing.DOWN) {
                        break Label_0164;
                    }
                }
                if (this.type == PlacementType.BLOCK) {
                    final Integer meta = this.mapping.get(direction);
                    if (((meta != null) ? meta : -1) != (this.maskMeta & metadata) && this.mapping.size() != 0) {
                        break Label_0164;
                    }
                }
                list.add(direction);
            }
        }
        final EnumFacing[] directions = new EnumFacing[list.size()];
        return list.toArray(directions);
    }
    
    static {
        VALID_DIRECTIONS = EnumFacing.values();
    }
    
    public enum PlacementType
    {
        BLOCK, 
        PLAYER, 
        PISTON;
    }
}
