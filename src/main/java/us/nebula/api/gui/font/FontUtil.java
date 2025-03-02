package us.nebula.api.gui.font;

import us.nebula.Nebula;

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
public final class FontUtil
{
    private static final Map<String, AWTFontRenderer> FONT_RENDERER_MAP = new HashMap<>();
    private static AWTFontRenderer defaultFontRenderer;

    static
    {
        registerCustomFonts();
    }

    public static void drawStringShadow(final String text, final double x, final double y, final int color)
    {
        defaultFontRenderer.drawStringShadow(text, x, y, color);
    }

    public static double getStringWidth(final String input)
    {
        return defaultFontRenderer.getStringWidth(input);
    }

    public static double getFontHeight()
    {
        return defaultFontRenderer.getFontHeight() / 2.0;
    }

    public static double getMiddlePoint(final double height, final double fontHeight)
    {
        return (height / 2.0) - (fontHeight / 2.0);
    }

    public static AWTFontRenderer getFont(final String family, final int size)
    {
        return FONT_RENDERER_MAP.get(family + "_" + size);
    }

    private static Font loadFont(final String location)
    {
        try (final InputStream stream = FontUtil.class.getResourceAsStream(location))
        {
            if (stream == null)
            {
                throw new RuntimeException("failed to create inputstream");
            }
            final GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
            final Font font = Font.createFont(TRUETYPE_FONT, stream);
            graphics.registerFont(font);
            Nebula.INSTANCE.getLogger().info("Loaded custom font family \"{}\"", font.getFamily());
            return font;
        } catch (final IOException | FontFormatException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void registerCustomFonts()
    {
        final Font poppinsFont = loadFont("/assets/minecraft/nebula/font/Poppins-Regular.ttf");
        FONT_RENDERER_MAP.put("poppins_18", defaultFontRenderer = new AWTFontRenderer(poppinsFont, 18));
        FONT_RENDERER_MAP.put("poppins_12", new AWTFontRenderer(poppinsFont, 12));
        FONT_RENDERER_MAP.put("icon_18", new AWTFontRenderer(loadFont("/assets/minecraft/nebula/font/Typeface.ttf"), 18));
    }
}
