package minimap;

import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.image.BufferedImage;

import static java.awt.image.BufferedImage.TYPE_INT_RGB;

/**
 * @author xgraza
 * @since 02/23/25
 */
public final class MinimapTexture extends DynamicTexture
{
    private final BufferedImage image;

    public MinimapTexture(final int w, final int h)
    {
        super(w, h, new int[w * h * 3]);
        image = new BufferedImage(w, h, TYPE_INT_RGB);
        updateTextureData();
    }

    public void setColor(final int x, final int y, final int argb)
    {
        image.setRGB(x, y, argb);
    }

    public void updateTextureData()
    {
        image.getRGB(0, 0, image.getWidth(), image.getHeight(), dynamicTextureData, 0, image.getWidth());
    }

    public BufferedImage getImage()
    {
        return image;
    }
}
