package lol.nebula.util.render.font;

import lol.nebula.Nebula;

import java.awt.*;
import java.io.IOException;
import java.io.InputStream;

import static java.awt.Font.TRUETYPE_FONT;

/**
 * @author aesthetical
 * @since 03/05/23
 */
public class Fonts {
    public static AWTFontRenderer axiforma;

    /**
     * Loads a font from an input stream, creates the font, and adds it to the graphics Environment
     * @param inputStream the input stream
     * @return the new Font object
     * @throws IOException if the input stream could not be created/closed
     * @throws FontFormatException if the input stream does not contain the right font information
     */
    private static Font loadFont(InputStream inputStream) throws IOException, FontFormatException {
        GraphicsEnvironment graphics = GraphicsEnvironment.getLocalGraphicsEnvironment();
        Font font = Font.createFont(TRUETYPE_FONT, inputStream);
        graphics.registerFont(font);
        inputStream.close();
        return font;
    }

    /**
     * Creates a new AWTFontRenderer
     * @param fontName the name of the font file
     * @param size the size to create the font with
     * @return the AWTFontRenderer object or null
     */
    private static AWTFontRenderer create(String fontName, int size) {
        InputStream is = Fonts.class.getResourceAsStream("/assets/minecraft/nebula/fonts/" + fontName + ".ttf");
        if (is != null) {
            try {
                Font font = loadFont(is);
                Nebula.getLogger().info("Loading {} from {}.ttf", font.getFontName(), fontName);
                return new AWTFontRenderer(font.deriveFont(Font.PLAIN, size));
            } catch (IOException | FontFormatException e) {
                Nebula.getLogger().error("Failed to load {}.ttf. Stacktrace is below:", fontName);
                e.printStackTrace();

                return null;
            }
        }

        return null;
    }

    /**
     * Creates the fonts the client uses
     */
    public static void loadFonts() {
        axiforma = create("axiforma", 17);
    }
}