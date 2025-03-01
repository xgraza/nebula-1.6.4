package us.nebula.api.gui.font;

import java.util.HashMap;
import java.util.Map;

/**
 * @author xgraza
 * @since 03/01/25
 */
public final class Fonts
{
    private static final Map<String, AWTFontRenderer> FONT_RENDERER_MAP = new HashMap<>();
    private static AWTFontRenderer defaultFontRenderer;

    static
    {
        defaultFontRenderer = new AWTFontRenderer("Verdana", 19);
        FONT_RENDERER_MAP.put("verdana_19", defaultFontRenderer);

        registerCustomFonts();
    }

    public static void drawStringShadow(final String text, final double x, final double y, final int color)
    {
        defaultFontRenderer.drawStringShadow(text, x, y, color);
    }

    public static AWTFontRenderer getFont(final String family, final int size)
    {
        return FONT_RENDERER_MAP.get(family + "_" + size);
    }

    private static void registerCustomFonts()
    {

    }
}
