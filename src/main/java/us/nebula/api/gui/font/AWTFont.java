package us.nebula.api.gui.font;

import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.*;
import java.awt.geom.Rectangle2D;
import java.awt.image.BufferedImage;

import static java.awt.RenderingHints.*;
import static java.awt.image.BufferedImage.TYPE_INT_ARGB;
import static org.lwjgl.opengl.GL11.*;

/**
 * @author xgraza
 * @since 02/28/25
 */
public final class AWTFont
{
    private final Font font;
    private final DynamicTexture glyphTexture;
    private final Glyph[] glyphBin;
    private double spaceWidth, fontHeight;

    public AWTFont(final Font font)
    {
        this.font = font;
        glyphBin = new Glyph[1500];
        glyphTexture = createGlyphTextureMap();
    }

    public void drawChar(final Glyph glyph, final double x, final double y)
    {
        glPushMatrix();
        glBegin(GL_QUADS);
        {
            glTexCoord2d(glyph.getX() / 1000.0, glyph.getY() / 512.0);
            glVertex2d(x, y);

            glTexCoord2d(glyph.getX() / 1000.0, (glyph.getY() + glyph.getHeight()) / 512.0);
            glVertex2d(x, y + glyph.getHeight());

            glTexCoord2d((glyph.getX() + glyph.getWidth()) / 1000.0, (glyph.getY() + glyph.getHeight()) / 512.0);
            glVertex2d(x + glyph.getWidth(), y + glyph.getHeight());

            glTexCoord2d((glyph.getX() + glyph.getWidth()) / 1000.0, glyph.getY() / 512.0);
            glVertex2d(x + glyph.getWidth(), y);
        }
        glEnd();
        glPopMatrix();
    }

    public Glyph getGlyph(final char codePoint)
    {
        return glyphBin[codePoint];
    }

    private DynamicTexture createGlyphTextureMap()
    {
        final BufferedImage image = new BufferedImage(1000, 512, TYPE_INT_ARGB);

        // create graphics environment
        final Graphics2D graphics = image.createGraphics();
        {
            graphics.setFont(font);
            graphics.setColor(Color.WHITE);
            graphics.setRenderingHint(KEY_FRACTIONALMETRICS, VALUE_FRACTIONALMETRICS_ON);
            graphics.setRenderingHint(KEY_INTERPOLATION, VALUE_INTERPOLATION_BILINEAR);
            graphics.setRenderingHint(KEY_TEXT_ANTIALIASING, VALUE_TEXT_ANTIALIAS_ON);
            graphics.setRenderingHint(KEY_ANTIALIASING, VALUE_ANTIALIAS_ON);
        }
        final FontMetrics metrics = graphics.getFontMetrics();

        spaceWidth = metrics.charWidth(' ');
        fontHeight = font.getSize() + metrics.getAscent();

        float x = 0;
        float y = font.getSize();
        for (char c = 32; c < glyphBin.length; ++c)
        {
            if (!font.canDisplay(c) || c == font.getMissingGlyphCode())
            {
                continue;
            }
            final Rectangle2D rect = metrics.getStringBounds(String.valueOf(c), graphics);
            if (x + rect.getWidth() >= 1000)
            {
                y += (float) fontHeight;
                x = 0;
            }

            final Glyph glyph = new Glyph(c, x, y, rect.getWidth() + 4.0f, rect.getHeight(), metrics.stringWidth(String.valueOf(c)) - metrics.charWidth(c));
            glyphBin[c] = glyph;

            graphics.drawString(String.valueOf(c), x + 2.0f, y + metrics.getAscent());
            x += (float) glyph.getWidth();
        }

        //fontHeight /= 2.0;

//        try
//        {
//            ImageIO.write(image, "png", new File(Nebula.INSTANCE.getNebulaRootDir(), "font_bin.png"));
//        } catch (IOException e)
//        {
//            throw new RuntimeException(e);
//        }

        return new DynamicTexture(image);
    }

    public DynamicTexture getGlyphTexture()
    {
        return glyphTexture;
    }

    public double getSpaceWidth()
    {
        return spaceWidth;
    }

    public double getFontHeight()
    {
        return fontHeight;
    }
}
