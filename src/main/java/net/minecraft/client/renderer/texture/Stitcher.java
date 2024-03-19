package net.minecraft.client.renderer.texture;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Set;
import net.minecraft.client.renderer.StitcherException;
import net.minecraft.util.MathHelper;

public class Stitcher
{
    private final int mipmapLevelStitcher;
    private final Set setStitchHolders = new HashSet(256);
    private final List stitchSlots = new ArrayList(256);
    private int currentWidth;
    private int currentHeight;
    private final int maxWidth;
    private final int maxHeight;
    private final boolean forcePowerOf2;

    /** Max size (width or height) of a single tile */
    private final int maxTileDimension;
    private static final String __OBFID = "CL_00001054";

    public Stitcher(int p_i45095_1_, int p_i45095_2_, boolean p_i45095_3_, int p_i45095_4_, int p_i45095_5_)
    {
        this.mipmapLevelStitcher = p_i45095_5_;
        this.maxWidth = p_i45095_1_;
        this.maxHeight = p_i45095_2_;
        this.forcePowerOf2 = p_i45095_3_;
        this.maxTileDimension = p_i45095_4_;
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
        Holder var2 = new Holder(par1TextureAtlasSprite, this.mipmapLevelStitcher);

        if (this.maxTileDimension > 0)
        {
            var2.setNewDimension(this.maxTileDimension);
        }

        this.setStitchHolders.add(var2);
    }

    public void doStitch()
    {
        Holder[] var1 = (Holder[])((Holder[])this.setStitchHolders.toArray(new Holder[this.setStitchHolders.size()]));
        Arrays.sort(var1);
        Holder[] var2 = var1;
        int var3 = var1.length;

        for (int var4 = 0; var4 < var3; ++var4)
        {
            Holder var5 = var2[var4];

            if (!this.allocateSlot(var5))
            {
                String var6 = String.format("Unable to fit: %s, size: %dx%d, atlas: %dx%d, atlasMax: %dx%d - Maybe try a lower resolution resourcepack?", new Object[] {var5.getAtlasSprite().getIconName(), Integer.valueOf(var5.getAtlasSprite().getIconWidth()), Integer.valueOf(var5.getAtlasSprite().getIconHeight()), Integer.valueOf(this.currentWidth), Integer.valueOf(this.currentHeight), Integer.valueOf(this.maxWidth), Integer.valueOf(this.maxHeight)});
                throw new StitcherException(var5, var6);
            }
        }

        if (this.forcePowerOf2)
        {
            this.currentWidth = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            this.currentHeight = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
        }
    }

    public List getStichSlots()
    {
        ArrayList var1 = Lists.newArrayList();
        Iterator var2 = this.stitchSlots.iterator();

        while (var2.hasNext())
        {
            Slot var7 = (Slot)var2.next();
            var7.getAllStitchSlots(var1);
        }

        ArrayList var71 = Lists.newArrayList();
        Iterator var8 = var1.iterator();

        while (var8.hasNext())
        {
            Slot var4 = (Slot)var8.next();
            Holder var5 = var4.getStitchHolder();
            TextureAtlasSprite var6 = var5.getAtlasSprite();
            var6.initSprite(this.currentWidth, this.currentHeight, var4.getOriginX(), var4.getOriginY(), var5.isRotated());
            var71.add(var6);
        }

        return var71;
    }

    private static int getMipmapDimension(int p_147969_0_, int p_147969_1_)
    {
        return (p_147969_0_ >> p_147969_1_) + ((p_147969_0_ & (1 << p_147969_1_) - 1) == 0 ? 0 : 1) << p_147969_1_;
    }

    /**
     * Attempts to find space for specified tile
     */
    private boolean allocateSlot(Holder par1StitchHolder)
    {
        for (int var2 = 0; var2 < this.stitchSlots.size(); ++var2)
        {
            if (((Slot)this.stitchSlots.get(var2)).addSlot(par1StitchHolder))
            {
                return true;
            }

            par1StitchHolder.rotate();

            if (((Slot)this.stitchSlots.get(var2)).addSlot(par1StitchHolder))
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
    private boolean expandAndAllocateSlot(Holder par1StitchHolder)
    {
        int var2 = Math.min(par1StitchHolder.getWidth(), par1StitchHolder.getHeight());
        boolean var3 = this.currentWidth == 0 && this.currentHeight == 0;
        boolean var4;
        int var5;

        if (this.forcePowerOf2)
        {
            var5 = MathHelper.roundUpToPowerOfTwo(this.currentWidth);
            int var14 = MathHelper.roundUpToPowerOfTwo(this.currentHeight);
            int var15 = MathHelper.roundUpToPowerOfTwo(this.currentWidth + var2);
            int var8 = MathHelper.roundUpToPowerOfTwo(this.currentHeight + var2);
            boolean var9 = var15 <= this.maxWidth;
            boolean var10 = var8 <= this.maxHeight;

            if (!var9 && !var10)
            {
                return false;
            }

            boolean var11 = var5 != var15;
            boolean var12 = var14 != var8;

            if (var11 ^ var12)
            {
                var4 = !var11;
            }
            else
            {
                var4 = var9 && var5 <= var14;
            }
        }
        else
        {
            boolean var141 = this.currentWidth + var2 <= this.maxWidth;
            boolean var151 = this.currentHeight + var2 <= this.maxHeight;

            if (!var141 && !var151)
            {
                return false;
            }

            var4 = var141 && (var3 || this.currentWidth <= this.currentHeight);
        }

        var5 = Math.max(par1StitchHolder.getWidth(), par1StitchHolder.getHeight());

        if (MathHelper.roundUpToPowerOfTwo((!var4 ? this.currentHeight : this.currentWidth) + var5) > (!var4 ? this.maxHeight : this.maxWidth))
        {
            return false;
        }
        else
        {
            Slot var142;

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

                var142 = new Slot(this.currentWidth, 0, par1StitchHolder.getWidth(), this.currentHeight);
                this.currentWidth += par1StitchHolder.getWidth();
            }
            else
            {
                var142 = new Slot(0, this.currentHeight, this.currentWidth, par1StitchHolder.getHeight());
                this.currentHeight += par1StitchHolder.getHeight();
            }

            var142.addSlot(par1StitchHolder);
            this.stitchSlots.add(var142);
            return true;
        }
    }

    public static class Slot
    {
        private final int originX;
        private final int originY;
        private final int width;
        private final int height;
        private List subSlots;
        private Holder holder;
        private static final String __OBFID = "CL_00001056";

        public Slot(int par1, int par2, int par3, int par4)
        {
            this.originX = par1;
            this.originY = par2;
            this.width = par3;
            this.height = par4;
        }

        public Holder getStitchHolder()
        {
            return this.holder;
        }

        public int getOriginX()
        {
            return this.originX;
        }

        public int getOriginY()
        {
            return this.originY;
        }

        public boolean addSlot(Holder par1StitchHolder)
        {
            if (this.holder != null)
            {
                return false;
            }
            else
            {
                int var2 = par1StitchHolder.getWidth();
                int var3 = par1StitchHolder.getHeight();

                if (var2 <= this.width && var3 <= this.height)
                {
                    if (var2 == this.width && var3 == this.height)
                    {
                        this.holder = par1StitchHolder;
                        return true;
                    }
                    else
                    {
                        if (this.subSlots == null)
                        {
                            this.subSlots = new ArrayList(1);
                            this.subSlots.add(new Slot(this.originX, this.originY, var2, var3));
                            int var8 = this.width - var2;
                            int var9 = this.height - var3;

                            if (var9 > 0 && var8 > 0)
                            {
                                int var6 = Math.max(this.height, var8);
                                int var7 = Math.max(this.width, var9);

                                if (var6 >= var7)
                                {
                                    this.subSlots.add(new Slot(this.originX, this.originY + var3, var2, var9));
                                    this.subSlots.add(new Slot(this.originX + var2, this.originY, var8, this.height));
                                }
                                else
                                {
                                    this.subSlots.add(new Slot(this.originX + var2, this.originY, var8, var3));
                                    this.subSlots.add(new Slot(this.originX, this.originY + var3, this.width, var9));
                                }
                            }
                            else if (var8 == 0)
                            {
                                this.subSlots.add(new Slot(this.originX, this.originY + var3, var2, var9));
                            }
                            else if (var9 == 0)
                            {
                                this.subSlots.add(new Slot(this.originX + var2, this.originY, var8, var3));
                            }
                        }

                        Iterator var81 = this.subSlots.iterator();

                        while (var81.hasNext())
                        {
                            Slot var91 = (Slot)var81.next();

                            if (var91.addSlot(par1StitchHolder))
                            {
                                return true;
                            }
                        }

                        return false;
                    }
                }
                else
                {
                    return false;
                }
            }
        }

        public void getAllStitchSlots(List par1List)
        {
            if (this.holder != null)
            {
                par1List.add(this);
            }
            else if (this.subSlots != null)
            {
                Iterator var2 = this.subSlots.iterator();

                while (var2.hasNext())
                {
                    Slot var3 = (Slot)var2.next();
                    var3.getAllStitchSlots(par1List);
                }
            }
        }

        public String toString()
        {
            return "Slot{originX=" + this.originX + ", originY=" + this.originY + ", width=" + this.width + ", height=" + this.height + ", texture=" + this.holder + ", subSlots=" + this.subSlots + '}';
        }
    }

    public static class Holder implements Comparable
    {
        private final TextureAtlasSprite theTexture;
        private final int width;
        private final int height;
        private final int mipmapLevelHolder;
        private boolean rotated;
        private float scaleFactor = 1.0F;
        private static final String __OBFID = "CL_00001055";

        public Holder(TextureAtlasSprite p_i45094_1_, int p_i45094_2_)
        {
            this.theTexture = p_i45094_1_;
            this.width = p_i45094_1_.getIconWidth();
            this.height = p_i45094_1_.getIconHeight();
            this.mipmapLevelHolder = p_i45094_2_;
            this.rotated = Stitcher.getMipmapDimension(this.height, p_i45094_2_) > Stitcher.getMipmapDimension(this.width, p_i45094_2_);
        }

        public TextureAtlasSprite getAtlasSprite()
        {
            return this.theTexture;
        }

        public int getWidth()
        {
            return this.rotated ? Stitcher.getMipmapDimension((int)((float)this.height * this.scaleFactor), this.mipmapLevelHolder) : Stitcher.getMipmapDimension((int)((float)this.width * this.scaleFactor), this.mipmapLevelHolder);
        }

        public int getHeight()
        {
            return this.rotated ? Stitcher.getMipmapDimension((int)((float)this.width * this.scaleFactor), this.mipmapLevelHolder) : Stitcher.getMipmapDimension((int)((float)this.height * this.scaleFactor), this.mipmapLevelHolder);
        }

        public void rotate()
        {
            this.rotated = !this.rotated;
        }

        public boolean isRotated()
        {
            return this.rotated;
        }

        public void setNewDimension(int par1)
        {
            if (this.width > par1 && this.height > par1)
            {
                this.scaleFactor = (float)par1 / (float)Math.min(this.width, this.height);
            }
        }

        public String toString()
        {
            return "Holder{width=" + this.width + ", height=" + this.height + '}';
        }

        public int compareTo(Holder par1StitchHolder)
        {
            int var2;

            if (this.getHeight() == par1StitchHolder.getHeight())
            {
                if (this.getWidth() == par1StitchHolder.getWidth())
                {
                    if (this.theTexture.getIconName() == null)
                    {
                        return par1StitchHolder.theTexture.getIconName() == null ? 0 : -1;
                    }

                    return this.theTexture.getIconName().compareTo(par1StitchHolder.theTexture.getIconName());
                }

                var2 = this.getWidth() < par1StitchHolder.getWidth() ? 1 : -1;
            }
            else
            {
                var2 = this.getHeight() < par1StitchHolder.getHeight() ? 1 : -1;
            }

            return var2;
        }

        public int compareTo(Object par1Obj)
        {
            return this.compareTo((Holder)par1Obj);
        }
    }
}
