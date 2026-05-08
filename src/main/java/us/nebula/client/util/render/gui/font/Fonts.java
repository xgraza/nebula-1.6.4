package us.nebula.client.util.render.gui.font;

import java.awt.Font;
import java.awt.FontFormatException;
import java.awt.GraphicsEnvironment;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

import static java.awt.Font.TRUETYPE_FONT;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class Fonts
{
    private static final String BASE_ASSET_LOCATION = "/assets/nebula/font/%s";

    public static final int LARGE_FONT_SIZE = 26;
    public static final int NORMAL_FONT_SIZE = 18;
    public static final int SMALL_FONT_SIZE = 11;

    private static final Map<String, Font> CUSTOM_FONT_CACHE = new HashMap<>();

    public static AWTFontRenderer POPPINS, POPPINS_SMALL, POPPINS_LARGE;
    public static AWTFontRenderer TYPEFACE, ICONFACE;

    public static double getMiddlePoint(final double height, final double fontHeight)
    {
        return (height / 2.0) - (fontHeight / 2.0);
    }

    public static void initFonts()
    {
        POPPINS = createFont(loadFont("Poppins-Regular"), NORMAL_FONT_SIZE);
        POPPINS_SMALL = createFont(loadFont("Poppins-Regular"), SMALL_FONT_SIZE);
        POPPINS_LARGE = createFont(loadFont("Poppins-Regular"), LARGE_FONT_SIZE);
        TYPEFACE = createFont(loadFont("Typeface"), NORMAL_FONT_SIZE);
        ICONFACE = createFont(loadFont("Iconface-Regular"), NORMAL_FONT_SIZE);
    }

    public static AWTFontRenderer createFont(final Font font, final int size)
    {
        return new AWTFontRenderer(font, size);
    }

    public static AWTFontRenderer createFont(final String fontName, final int size)
    {
        final Font font = CUSTOM_FONT_CACHE.getOrDefault(fontName,
                new Font(fontName, Font.PLAIN, size));
        if (font == null)
        {
            throw new RuntimeException("Failed to create font " + fontName);
        }
        return createFont(font, size);
    }

    private static Font loadFont(final String fontName)
    {
        if (CUSTOM_FONT_CACHE.containsKey(fontName))
        {
            return CUSTOM_FONT_CACHE.get(fontName);
        }
        final String location = String.format(BASE_ASSET_LOCATION, fontName + ".ttf");
        try (final InputStream stream = Fonts.class.getResourceAsStream(location))
        {
            if (stream == null)
            {
                throw new RuntimeException("failed to create inputStream for font " + location);
            }
            final GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final Font font = Font.createFont(TRUETYPE_FONT, stream);
            if (!graphics.registerFont(font))
            {
                throw new RuntimeException("failed to create font from " + location);
            }
            CUSTOM_FONT_CACHE.put(fontName, font);
            return font;
        } catch (final IOException | FontFormatException e)
        {
            throw new RuntimeException(e);
        }
    }
}
