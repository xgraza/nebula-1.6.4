package net.minecraft.src;

import com.google.common.collect.Lists;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.nio.IntBuffer;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.imageio.ImageIO;
import org.lwjgl.opengl.GL11;
import org.lwjgl.opengl.GL12;

public class TextureAtlasSprite implements Icon
{
    private final String iconName;
    protected List framesTextureData = Lists.newArrayList();
    private AnimationMetadataSection animationMetadata;
    protected boolean rotated;
    protected int originX;
    protected int originY;
    protected int width;
    protected int height;
    private float minU;
    private float maxU;
    private float minV;
    private float maxV;
    protected int frameCounter;
    protected int tickCounter;
    private int indexInMap = -1;
    public float baseU;
    public float baseV;
    public int sheetWidth;
    public int sheetHeight;
    private boolean mipmapActive = false;
    public int glOwnTextureId = -1;
    private int uploadedFrameIndex = -1;
    private int uploadedOwnFrameIndex = -1;
    public IntBuffer[] frameBuffers;
    public Mipmaps[] frameMipmaps;

    protected TextureAtlasSprite(String par1Str)
    {
        this.iconName = par1Str;
    }

    public void initSprite(int par1, int par2, int par3, int par4, boolean par5)
    {
        this.originX = par3;
        this.originY = par4;
        this.rotated = par5;
        float var6 = (float)(0.009999999776482582D / (double)par1);
        float var7 = (float)(0.009999999776482582D / (double)par2);
        this.minU = (float)par3 / (float)((double)par1) + var6;
        this.maxU = (float)(par3 + this.width) / (float)((double)par1) - var6;
        this.minV = (float)par4 / (float)par2 + var7;
        this.maxV = (float)(par4 + this.height) / (float)par2 - var7;
        this.baseU = Math.min(this.minU, this.maxU);
        this.baseV = Math.min(this.minV, this.maxV);
    }

    public void copyFrom(TextureAtlasSprite par1TextureAtlasSprite)
    {
        this.originX = par1TextureAtlasSprite.originX;
        this.originY = par1TextureAtlasSprite.originY;
        this.width = par1TextureAtlasSprite.width;
        this.height = par1TextureAtlasSprite.height;
        this.rotated = par1TextureAtlasSprite.rotated;
        this.minU = par1TextureAtlasSprite.minU;
        this.maxU = par1TextureAtlasSprite.maxU;
        this.minV = par1TextureAtlasSprite.minV;
        this.maxV = par1TextureAtlasSprite.maxV;
        this.baseU = Math.min(this.minU, this.maxU);
        this.baseV = Math.min(this.minV, this.maxV);
    }

    /**
     * Returns the X position of this icon on its texture sheet, in pixels.
     */
    public int getOriginX()
    {
        return this.originX;
    }

    /**
     * Returns the Y position of this icon on its texture sheet, in pixels.
     */
    public int getOriginY()
    {
        return this.originY;
    }

    /**
     * Returns the width of the icon, in pixels.
     */
    public int getIconWidth()
    {
        return this.width;
    }

    /**
     * Returns the height of the icon, in pixels.
     */
    public int getIconHeight()
    {
        return this.height;
    }

    /**
     * Returns the minimum U coordinate to use when rendering with this icon.
     */
    public float getMinU()
    {
        return this.minU;
    }

    /**
     * Returns the maximum U coordinate to use when rendering with this icon.
     */
    public float getMaxU()
    {
        return this.maxU;
    }

    /**
     * Gets a U coordinate on the icon. 0 returns uMin and 16 returns uMax. Other arguments return in-between values.
     */
    public float getInterpolatedU(double par1)
    {
        float var3 = this.maxU - this.minU;
        return this.minU + var3 * (float)par1 / 16.0F;
    }

    /**
     * Returns the minimum V coordinate to use when rendering with this icon.
     */
    public float getMinV()
    {
        return this.minV;
    }

    /**
     * Returns the maximum V coordinate to use when rendering with this icon.
     */
    public float getMaxV()
    {
        return this.maxV;
    }

    /**
     * Gets a V coordinate on the icon. 0 returns vMin and 16 returns vMax. Other arguments return in-between values.
     */
    public float getInterpolatedV(double par1)
    {
        float var3 = this.maxV - this.minV;
        return this.minV + var3 * ((float)par1 / 16.0F);
    }

    public String getIconName()
    {
        return this.iconName;
    }

    public void updateAnimation()
    {
        ++this.tickCounter;

        if (this.tickCounter >= this.animationMetadata.getFrameTimeSingle(this.frameCounter))
        {
            int var1 = this.animationMetadata.getFrameIndex(this.frameCounter);
            int var2 = this.animationMetadata.getFrameCount() == 0 ? this.framesTextureData.size() : this.animationMetadata.getFrameCount();
            this.frameCounter = (this.frameCounter + 1) % var2;
            this.tickCounter = 0;
            int var3 = this.animationMetadata.getFrameIndex(this.frameCounter);

            if (var1 != var3 && var3 >= 0 && var3 < this.framesTextureData.size())
            {
                this.uploadFrameTexture(var3, this.originX, this.originY);
                this.uploadedFrameIndex = var3;
            }
        }
    }

    public int[] getFrameTextureData(int par1)
    {
        return (int[])((int[])this.framesTextureData.get(par1));
    }

    public int getFrameCount()
    {
        return this.framesTextureData.size();
    }

    public void setIconWidth(int par1)
    {
        this.width = par1;
    }

    public void setIconHeight(int par1)
    {
        this.height = par1;
    }

    public void loadSprite(Resource par1Resource) throws IOException
    {
        this.resetSprite();
        InputStream var2 = par1Resource.getInputStream();
        AnimationMetadataSection var3 = (AnimationMetadataSection)par1Resource.getMetadata("animation");
        BufferedImage var4 = ImageIO.read(var2);
        this.height = var4.getHeight();
        this.width = var4.getWidth();
        int[] var5 = new int[this.height * this.width];
        var4.getRGB(0, 0, this.width, this.height, var5, 0, this.width);
        int var6;

        if (var3 == null)
        {
            if (this.height != this.width)
            {
                throw new RuntimeException("broken aspect ratio and not an animation");
            }

            this.framesTextureData.add(var5);
        }
        else
        {
            var6 = this.height / this.width;
            int var7 = this.width;
            int var8 = this.width;
            this.height = this.width;
            int var9;

            if (var3.getFrameCount() > 0)
            {
                Iterator var10 = var3.getFrameIndexSet().iterator();

                while (var10.hasNext())
                {
                    var9 = ((Integer)var10.next()).intValue();

                    if (var9 >= var6)
                    {
                        throw new RuntimeException("invalid frameindex " + var9);
                    }

                    this.allocateFrameTextureData(var9);
                    this.framesTextureData.set(var9, getFrameTextureData(var5, var7, var8, var9));
                }

                this.animationMetadata = var3;
            }
            else
            {
                ArrayList var11 = Lists.newArrayList();

                for (var9 = 0; var9 < var6; ++var9)
                {
                    this.framesTextureData.add(getFrameTextureData(var5, var7, var8, var9));
                    var11.add(new AnimationFrame(var9, -1));
                }

                this.animationMetadata = new AnimationMetadataSection(var11, this.width, this.height, var3.getFrameTime());
            }
        }

        for (var6 = 0; var6 < this.framesTextureData.size(); ++var6)
        {
            if (this.framesTextureData.get(var6) == null)
            {
                this.framesTextureData.set(var6, new int[this.width * this.height]);
            }

            int[] var12 = (int[])((int[])this.framesTextureData.get(var6));
            this.fixTransparentColor(var12);
        }
    }

    private void allocateFrameTextureData(int par1)
    {
        if (this.framesTextureData.size() <= par1)
        {
            for (int var2 = this.framesTextureData.size(); var2 <= par1; ++var2)
            {
                this.framesTextureData.add((Object)null);
            }
        }
    }

    private static int[] getFrameTextureData(int[] par0ArrayOfInteger, int par1, int par2, int par3)
    {
        int[] var4 = new int[par1 * par2];
        System.arraycopy(par0ArrayOfInteger, par3 * var4.length, var4, 0, var4.length);
        return var4;
    }

    public void clearFramesTextureData()
    {
        this.framesTextureData.clear();
    }

    public boolean hasAnimationMetadata()
    {
        return this.animationMetadata != null;
    }

    public void setFramesTextureData(List par1List)
    {
        this.framesTextureData = par1List;

        for (int var2 = 0; var2 < this.framesTextureData.size(); ++var2)
        {
            if (this.framesTextureData.get(var2) == null)
            {
                this.framesTextureData.set(var2, new int[this.width * this.height]);
            }
        }
    }

    private void resetSprite()
    {
        this.animationMetadata = null;
        this.setFramesTextureData(Lists.newArrayList());
        this.frameCounter = 0;
        this.tickCounter = 0;
        this.deleteOwnTexture();
        this.uploadedFrameIndex = -1;
        this.uploadedOwnFrameIndex = -1;
        this.frameBuffers = null;
        this.frameMipmaps = null;
    }

    public String toString()
    {
        return "TextureAtlasSprite{name=\'" + this.iconName + '\'' + ", frameCount=" + this.framesTextureData.size() + ", rotated=" + this.rotated + ", x=" + this.originX + ", y=" + this.originY + ", height=" + this.height + ", width=" + this.width + ", u0=" + this.minU + ", u1=" + this.maxU + ", v0=" + this.minV + ", v1=" + this.maxV + '}';
    }

    public int getWidth()
    {
        return this.width;
    }

    public int getHeight()
    {
        return this.height;
    }

    public int getIndexInMap()
    {
        return this.indexInMap;
    }

    public void setIndexInMap(int indexInMap)
    {
        this.indexInMap = indexInMap;
    }

    public void setMipmapActive(boolean mipmapActive)
    {
        this.mipmapActive = mipmapActive;
        this.frameMipmaps = null;
    }

    public boolean load(ResourceManager manager, ResourceLocation location) throws IOException
    {
        this.loadSprite(manager.getResource(location));
        return true;
    }

    public void uploadFrameTexture()
    {
        this.uploadFrameTexture(this.frameCounter, this.originX, this.originY);
    }

    public void uploadFrameTexture(int frameIndex, int xPos, int yPos)
    {
        int frameCount = this.getFrameCount();

        if (frameIndex >= 0 && frameIndex < frameCount)
        {
            if (frameCount <= 1)
            {
                int[] buf = this.getFrameTextureData(frameIndex);
                IntBuffer data = TextureUtils.getStaticBuffer(this.width, this.height);
                data.clear();
                data.put(buf);
                data.clear();
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, xPos, yPos, this.width, this.height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, data);
            }
            else
            {
                if (this.frameBuffers == null)
                {
                    this.frameBuffers = new IntBuffer[frameCount];

                    for (int var8 = 0; var8 < this.frameBuffers.length; ++var8)
                    {
                        int[] var10 = this.getFrameTextureData(var8);
                        IntBuffer buf1 = GLAllocation.createDirectIntBuffer(var10.length);
                        buf1.put(var10);
                        buf1.clear();
                        this.frameBuffers[var8] = buf1;
                    }
                }

                IntBuffer var9 = this.frameBuffers[frameIndex];
                var9.clear();
                GL11.glTexSubImage2D(GL11.GL_TEXTURE_2D, 0, xPos, yPos, this.width, this.height, GL12.GL_BGRA, GL12.GL_UNSIGNED_INT_8_8_8_8_REV, var9);
            }

            if (this.mipmapActive)
            {
                this.uploadFrameMipmaps(frameIndex, xPos, yPos);
            }
        }
    }

    private void uploadFrameMipmaps(int frameIndex, int xPos, int yPos)
    {
        if (this.mipmapActive)
        {
            int frameCount = this.getFrameCount();

            if (frameIndex >= 0 && frameIndex < frameCount)
            {
                if (frameCount <= 1)
                {
                    int[] var9 = this.getFrameTextureData(frameIndex);
                    boolean var10 = false;
                    Mipmaps var11 = new Mipmaps(this.iconName, this.width, this.height, var9, var10);
                    var11.uploadMipmaps(xPos, yPos);
                }
                else
                {
                    if (this.frameMipmaps == null)
                    {
                        this.frameMipmaps = new Mipmaps[frameCount];

                        for (int m = 0; m < this.frameMipmaps.length; ++m)
                        {
                            int[] data = this.getFrameTextureData(m);
                            boolean direct = frameCount > 0;
                            this.frameMipmaps[m] = new Mipmaps(this.iconName, this.width, this.height, data, direct);
                        }
                    }

                    Mipmaps var8 = this.frameMipmaps[frameIndex];
                    var8.uploadMipmaps(xPos, yPos);
                }
            }
        }
    }

    public void bindOwnTexture()
    {
        if (this.glOwnTextureId < 0)
        {
            this.glOwnTextureId = TextureUtil.glGenTextures();
            TextureUtil.allocateTexture(this.glOwnTextureId, this.width, this.height);
            TextureUtils.setupTexture(this.width, this.height, 1, this.mipmapActive);
        }

        TextureUtil.bindTexture(this.glOwnTextureId);
    }

    public void bindUploadOwnTexture()
    {
        this.bindOwnTexture();
        this.uploadFrameTexture(this.frameCounter, 0, 0);
    }

    public void uploadOwnAnimation()
    {
        if (this.uploadedFrameIndex != this.uploadedOwnFrameIndex)
        {
            TextureUtil.bindTexture(this.glOwnTextureId);
            this.uploadFrameTexture(this.uploadedFrameIndex, 0, 0);
            this.uploadedOwnFrameIndex = this.uploadedFrameIndex;
        }
    }

    public void deleteOwnTexture()
    {
        if (this.glOwnTextureId >= 0)
        {
            GL11.glDeleteTextures(this.glOwnTextureId);
            this.glOwnTextureId = -1;
        }
    }

    private void fixTransparentColor(int[] data)
    {
        long redSum = 0L;
        long greenSum = 0L;
        long blueSum = 0L;
        long count = 0L;
        int redAvg;
        int greenAvg;
        int blueAvg;
        int i;
        int col;
        int alpha;

        for (redAvg = 0; redAvg < data.length; ++redAvg)
        {
            greenAvg = data[redAvg];
            blueAvg = greenAvg >> 24 & 255;

            if (blueAvg != 0)
            {
                i = greenAvg >> 16 & 255;
                col = greenAvg >> 8 & 255;
                alpha = greenAvg & 255;
                redSum += (long)i;
                greenSum += (long)col;
                blueSum += (long)alpha;
                ++count;
            }
        }

        if (count > 0L)
        {
            redAvg = (int)(redSum / count);
            greenAvg = (int)(greenSum / count);
            blueAvg = (int)(blueSum / count);

            for (i = 0; i < data.length; ++i)
            {
                col = data[i];
                alpha = col >> 24 & 255;

                if (alpha == 0)
                {
                    data[i] = redAvg << 16 | greenAvg << 8 | blueAvg;
                }
            }
        }
    }
}
