package net.minecraft.src;

import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.awt.image.DataBufferInt;
import java.awt.image.ImageObserver;

public class ImageBufferDownload implements IImageBuffer
{
    private int[] imageData;
    private int imageWidth;
    private int imageHeight;

    public BufferedImage parseUserSkin(BufferedImage par1BufferedImage)
    {
        if (par1BufferedImage == null)
        {
            return null;
        }
        else
        {
            this.imageWidth = 64;
            this.imageHeight = 32;
            int var3 = par1BufferedImage.getWidth();
            int var4 = par1BufferedImage.getHeight();

            if (var3 != 64 || var4 != 32 && var4 != 64)
            {
                while (this.imageWidth < var3 || this.imageHeight < var4)
                {
                    this.imageWidth *= 2;
                    this.imageHeight *= 2;
                }
            }

            BufferedImage var5 = new BufferedImage(this.imageWidth, this.imageHeight, 2);
            Graphics var6 = var5.getGraphics();
            var6.drawImage(par1BufferedImage, 0, 0, (ImageObserver)null);
            var6.dispose();
            this.imageData = ((DataBufferInt)var5.getRaster().getDataBuffer()).getData();
            int var7 = this.imageWidth;
            int var8 = this.imageHeight;
            this.setAreaOpaque(0, 0, var7 / 2, var8 / 2);
            this.setAreaTransparent(var7 / 2, 0, var7, var8);
            this.setAreaOpaque(0, var8 / 2, var7, var8);
            return var5;
        }
    }

    /**
     * Makes the given area of the image transparent if it was previously completely opaque (used to remove the outer
     * layer of a skin around the head if it was saved all opaque; this would be redundant so it's assumed that the skin
     * maker is just using an image editor without an alpha channel)
     */
    private void setAreaTransparent(int par1, int par2, int par3, int par4)
    {
        if (!this.hasTransparency(par1, par2, par3, par4))
        {
            for (int var5 = par1; var5 < par3; ++var5)
            {
                for (int var6 = par2; var6 < par4; ++var6)
                {
                    this.imageData[var5 + var6 * this.imageWidth] &= 16777215;
                }
            }
        }
    }

    /**
     * Makes the given area of the image opaque
     */
    private void setAreaOpaque(int par1, int par2, int par3, int par4)
    {
        for (int var5 = par1; var5 < par3; ++var5)
        {
            for (int var6 = par2; var6 < par4; ++var6)
            {
                this.imageData[var5 + var6 * this.imageWidth] |= -16777216;
            }
        }
    }

    /**
     * Returns true if the given area of the image contains transparent pixels
     */
    private boolean hasTransparency(int par1, int par2, int par3, int par4)
    {
        for (int var5 = par1; var5 < par3; ++var5)
        {
            for (int var6 = par2; var6 < par4; ++var6)
            {
                int var7 = this.imageData[var5 + var6 * this.imageWidth];

                if ((var7 >> 24 & 255) < 128)
                {
                    return true;
                }
            }
        }

        return false;
    }
}
