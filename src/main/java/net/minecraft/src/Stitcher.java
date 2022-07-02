package net.minecraft.src;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class Stitcher
{
    private final Set setStitchHolders;
    private final List stitchSlots;
    private int currentWidth;
    private int currentHeight;
    private final int maxWidth;
    private final int maxHeight;
    private final boolean forcePowerOf2;

    /** Max size (width or height) of a single tile */
    private final int maxTileDimension;
    public int minTileDimension;

    public Stitcher(int par1, int par2, boolean par3)
    {
        this(par1, par2, par3, 0);
    }

    public Stitcher(int par1, int par2, boolean par3, int par4)
    {
        this.setStitchHolders = new HashSet(256);
        this.stitchSlots = new ArrayList(256);
        this.maxWidth = par1;
        this.maxHeight = par2;
        this.forcePowerOf2 = par3;
        this.maxTileDimension = par4;
    }

    public int getCurrentWidth()
    {
        return this.currentWidth;
    }

    public int getCurrentHeight()
    {
        return this.currentHeight;
    }

    public void addSprite(TextureAtlasSprite par1TextureAtlasSprite)
    {
        StitchHolder var2 = new StitchHolder(par1TextureAtlasSprite);

        if (this.maxTileDimension > 0)
        {
            var2.setNewDimension(this.maxTileDimension);
        }

        if (this.minTileDimension > 0)
        {
            var2.setMinDimension(this.minTileDimension);
        }

        this.setStitchHolders.add(var2);
    }

    public void doStitch()
    {
        StitchHolder[] var1 = (StitchHolder[])((StitchHolder[])this.setStitchHolders.toArray(new StitchHolder[this.setStitchHolders.size()]));
        Arrays.sort(var1);
        StitchHolder[] var2 = var1;
        int var3 = var1.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            StitchHolder var5 = var2[var4];

            if (!this.allocateSlot(var5))
            {
                String var6 = String.format("Unable to fit: %s - size: %dx%d - Maybe try a lowerresolution texturepack?", new Object[] {var5.getAtlasSprite().getIconName(), Integer.valueOf(var5.getAtlasSprite().getIconWidth()), Integer.valueOf(var5.getAtlasSprite().getIconHeight())});
                throw new StitcherException(var5, var6);
            }
        }

        if (this.forcePowerOf2)
        {
            this.currentWidth = this.getCeilPowerOf2(this.currentWidth);
            this.currentHeight = this.getCeilPowerOf2(this.currentHeight);
        }
    }

    public List getStichSlots()
    {
        ArrayList var1 = Lists.newArrayList();
        Iterator var2 = this.stitchSlots.iterator();

        while (var2.hasNext())
        {
            StitchSlot var3 = (StitchSlot)var2.next();
            var3.getAllStitchSlots(var1);
        }

        ArrayList var8 = Lists.newArrayList();
        Iterator var4 = var1.iterator();

        while (var4.hasNext())
        {
            StitchSlot var5 = (StitchSlot)var4.next();
            StitchHolder var6 = var5.getStitchHolder();
            TextureAtlasSprite var7 = var6.getAtlasSprite();
            var7.initSprite(this.currentWidth, this.currentHeight, var5.getOriginX(), var5.getOriginY(), var6.isRotated());
            var8.add(var7);
        }

        return var8;
    }

    /**
     * Returns power of 2 >= the specified value
     */
    private int getCeilPowerOf2(int par1)
    {
        int var2 = par1 - 1;
        var2 |= var2 >> 1;
        var2 |= var2 >> 2;
        var2 |= var2 >> 4;
        var2 |= var2 >> 8;
        var2 |= var2 >> 16;
        return var2 + 1;
    }

    /**
     * Attempts to find space for specified tile
     */
    private boolean allocateSlot(StitchHolder par1StitchHolder)
    {
        for (int var2 = 0; var2 < this.stitchSlots.size(); ++var2)
        {
            if (((StitchSlot)this.stitchSlots.get(var2)).addSlot(par1StitchHolder))
            {
                return true;
            }

            par1StitchHolder.rotate();

            if (((StitchSlot)this.stitchSlots.get(var2)).addSlot(par1StitchHolder))
            {
                return true;
            }

            par1StitchHolder.rotate();
        }

        return this.expandAndAllocateSlot(par1StitchHolder);
    }

    /**
     * Expand stitched texture in order to make space for specified tile
     */
    private boolean expandAndAllocateSlot(StitchHolder par1StitchHolder)
    {
        int var2 = Math.min(par1StitchHolder.getHeight(), par1StitchHolder.getWidth());
        boolean var3 = this.currentWidth == 0 && this.currentHeight == 0;
        boolean var4;

        if (this.forcePowerOf2)
        {
            int var5 = this.getCeilPowerOf2(this.currentWidth);
            int var6 = this.getCeilPowerOf2(this.currentHeight);
            int var7 = this.getCeilPowerOf2(this.currentWidth + var2);
            int var8 = this.getCeilPowerOf2(this.currentHeight + var2);
            boolean var9 = var7 <= this.maxWidth;
            boolean var10 = var8 <= this.maxHeight;

            if (!var9 && !var10)
            {
                return false;
            }

            int var11 = Math.max(par1StitchHolder.getHeight(), par1StitchHolder.getWidth());

            if (var3 && !var9 && this.getCeilPowerOf2(this.currentHeight + var11) > this.maxHeight)
            {
                return false;
            }

            boolean var12 = var5 != var7;
            boolean var13 = var6 != var8;
            boolean var10000;

            if (var12 ^ var13)
            {
                if (var12 && var9)
                {
                    var10000 = true;
                }
                else
                {
                    var10000 = false;
                }
            }
            else if (var9 && var5 <= var6)
            {
                var10000 = true;
            }
            else
            {
                var10000 = false;
            }

            int var14 = this.getCeilPowerOf2(this.currentWidth + var2);
            int var15 = this.getCeilPowerOf2(this.currentHeight + var2);
            var4 = var14 <= var15;
        }
        else
        {
            boolean var16 = this.currentWidth + var2 <= this.maxWidth;
            boolean var18 = this.currentHeight + var2 <= this.maxHeight;

            if (!var16 && !var18)
            {
                return false;
            }

            var4 = (var3 || this.currentWidth <= this.currentHeight) && var16;
        }

        StitchSlot var17;

        if (var4)
        {
            if (par1StitchHolder.getWidth() > par1StitchHolder.getHeight())
            {
                par1StitchHolder.rotate();
            }

            if (this.currentHeight == 0)
            {
                this.currentHeight = par1StitchHolder.getHeight();
            }

            var17 = new StitchSlot(this.currentWidth, 0, par1StitchHolder.getWidth(), this.currentHeight);
            this.currentWidth += par1StitchHolder.getWidth();
        }
        else
        {
            var17 = new StitchSlot(0, this.currentHeight, this.currentWidth, par1StitchHolder.getHeight());
            this.currentHeight += par1StitchHolder.getHeight();
        }

        var17.addSlot(par1StitchHolder);
        this.stitchSlots.add(var17);
        return true;
    }

    private int floorPowerOf2(int val)
    {
        int ceilPo2 = this.getCeilPowerOf2(val);
        return val < ceilPo2 ? ceilPo2 / 2 : ceilPo2;
    }
}
