package lol.nebula.util.render.font;

import net.minecraft.client.renderer.texture.DynamicTexture;

import java.awt.*;
import java.awt.image.BufferedImage;

import static org.lwjgl.opengl.GL11.*;

public class AWTFont {
    public static final int CHAR_OFFSET = 8;

    private final Font font;
    private final DynamicTexture texture;
    private final CharData[] charDataCache;

    private int bitmapHeight;
    public int FONT_HEIGHT;

    public AWTFont(Font font) {
        this.font = font;
        charDataCache = new CharData[512];
        texture = createBitmap();
        FONT_HEIGHT = font.getSize() / 2;
    }

    public CharData getCharacter(int c) {
        try {
            return charDataCache[c];
        } catch (IndexOutOfBoundsException ignored) {
            return null;
        }
    }

    public void drawChar(CharData characterLocation, double x, double y) {

        float width = characterLocation.width;
        float height = characterLocation.height;

        float textureX = characterLocation.x / 512.0f;
        float textureY = characterLocation.y / (float) bitmapHeight;
        float textureWidth = width / 512.0f;
        float textureHeight = height / (float) bitmapHeight;

        glBegin(GL_QUADS);
        {
            glTexCoord2d(textureX, textureY);
            glVertex2d(x, y);

            glTexCoord2d(textureX, textureY + textureHeight);
            glVertex2d(x, y + height);

            glTexCoord2d(textureX + textureWidth, textureY + textureHeight);
            glVertex2d(x + width, y + height);

            glTexCoord2d(textureX + textureWidth, textureY);
            glVertex2d(x + width, y);
        }
        glEnd();
    }

    private DynamicTexture createBitmap() {
        BufferedImage image = new BufferedImage(1, 1, BufferedImage.TYPE_INT_ARGB);
        Graphics2D graphics = applySettings(image);
        FontMetrics metrics = graphics.getFontMetrics();

        int x = 0, y = 0, height = 0;
        // populate our char data array, so we can draw a properly sized bitmap with all the drawable characters

        // 0-31 are all characters that we dont care about rendering
        // https://www.asciitable.com/asciifull.gif
        for (int i = 32; i < charDataCache.length; ++i) {
            char c = (char) i;
            if (!font.canDisplay(i) || i == font.getMissingGlyphCode()) {
                continue;
            }

            Rectangle bounds = metrics.getStringBounds(String.valueOf(c), graphics).getBounds();

            CharData characterLocation = new CharData();
            characterLocation.character = c;
            characterLocation.width = bounds.width + CHAR_OFFSET;
            characterLocation.height = bounds.height;

            if (x + characterLocation.width >= 512) {
                x = 0;
                y += height;
                height = 0;
            }

            int total = characterLocation.height + metrics.getAscent();
            if (total > height) {
                height = total;
                FONT_HEIGHT = height / 2;
            }

            characterLocation.x = x;
            characterLocation.y = y;

            charDataCache[i] = characterLocation;
            graphics.drawString(String.valueOf(c), x + 2, y + metrics.getAscent());
            x += characterLocation.width;
        }

        // dispose dummy graphics
        graphics.dispose();

        // draw characters on the properly sized bitmap
        image = new BufferedImage(512, bitmapHeight = (y + height), BufferedImage.TYPE_INT_ARGB);
        graphics = applySettings(image);
        metrics = graphics.getFontMetrics();

        for (CharData data : charDataCache) {
            if (data == null) {
                continue;
            }

            graphics.drawString(String.valueOf(data.character), data.x + 2, data.y + metrics.getAscent());
        }

        graphics.dispose();

        return new DynamicTexture(image);
    }

    private Graphics2D applySettings(BufferedImage image) {
        Graphics2D graphics = image.createGraphics();

        graphics.setFont(font);
        graphics.setColor(Color.WHITE);

        //graphics.setRenderingHint(RenderingHints.KEY_FRACTIONALMETRICS, RenderingHints.VALUE_FRACTIONALMETRICS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_INTERPOLATION, RenderingHints.VALUE_INTERPOLATION_BILINEAR);
        graphics.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        graphics.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

        return graphics;
    }

    public DynamicTexture getTexture() {
        return texture;
    }

    public CharData[] getCharDataCache() {
        return charDataCache;
    }

    public int getStringWidth(String str) {
        int width = 0;
        for (char c : str.toCharArray()) {
            width += getCharWidth(c);
        }
        return width;
    }

    public int getCharWidth(char c) {
        if (c != '\u00a7') {
            return charDataCache[c].width - CHAR_OFFSET;
        }

        return 0;
    }
}

