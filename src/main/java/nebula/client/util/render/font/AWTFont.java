package nebula.client.util.render.font;

import net.minecraft.client.renderer.texture.DynamicTexture;

import javax.imageio.ImageIO;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

import static org.lwjgl.opengl.GL11.*;

/**
 * @author Gavin
 * @since 08/09/23
 */
public class AWTFont {

  /**
   * The size of the glyph bin of this font renderer
   */
  public static final int GLYPH_BIN_SIZE = 256;

  /**
   * The width of the bitmap texture
   */
  public static final double BITMAP_WIDTH = 512.0;

  private final Font font;
  private DynamicTexture bitmapTexture;

  private final Glyph[] glyphs = new Glyph[GLYPH_BIN_SIZE];
  private double bitmapHeight;
  private int spaceWidth, fontHeight;

  public AWTFont(Font font) {
    this.font = font;
    generateTexture();
  }

  public double renderCharAt(char c, double x, double y) {
    if (c == ' ') return spaceWidth;

    Glyph glyph = glyph(c);
    if (glyph == null) return 0.0;

    double textureX = glyph.x() / BITMAP_WIDTH;
    double textureY = (glyph.y() / bitmapHeight);
    double textureWidth = glyph.width() / BITMAP_WIDTH;
    double textureHeight = glyph.height() / bitmapHeight;

    glBegin(GL_QUADS);
    {
      glTexCoord2d(textureX, textureY);
      glVertex2d(x, y);

      glTexCoord2d(textureX, textureY + textureHeight);
      glVertex2d(x, y + glyph.height());

      glTexCoord2d(textureX + textureWidth, textureY + textureHeight);
      glVertex2d(x + glyph.width(), y + glyph.height());

      glTexCoord2d(textureX + textureWidth, textureY);
      glVertex2d(x + glyph.width(), y);
    }
    glEnd();

    return glyph.width();
  }

  private void generateTexture() {

    double x = 0.0;
    double y = 0.0;

    double fontHeight = 0.0;

    BufferedImage[] images = new BufferedImage[GLYPH_BIN_SIZE];
    for (int charIndex = 33; charIndex < GLYPH_BIN_SIZE; ++charIndex) {
      char character = (char) charIndex;
      if (!font.canDisplay(character)) continue;

      BufferedImage texture = glyphTexture(character);
      if (texture == null) continue;

      if (x + texture.getWidth() > BITMAP_WIDTH) {
        x = 0.0;
        y += fontHeight;
      }

      if (texture.getHeight() > fontHeight) {
        fontHeight = texture.getHeight();
      }

      glyphs[character] = new Glyph(character, x, y,
        texture.getWidth(), texture.getHeight());
      images[character] = texture;

      x += texture.getWidth();
    }

    BufferedImage image = new BufferedImage((int) BITMAP_WIDTH,
      (int) (y + fontHeight), BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = (Graphics2D) image.getGraphics();
    applyGraphicSettings(graphics);

    for (Glyph glyph : glyphs) {
      if (glyph == null) continue;
      BufferedImage img = images[glyph.c()];
      if (img == null) continue;

      graphics.drawImage(img, (int) glyph.x(), (int) glyph.y(), null);
    }

    debugTexture(image, "512_bitmap.png");

    spaceWidth = (int) graphics.getFontMetrics()
      .getStringBounds(" ", graphics)
      .getBounds()
      .getWidth();

    bitmapTexture = new DynamicTexture(image);
    bitmapHeight = image.getHeight();
    graphics.dispose();

    this.fontHeight = (int) ((fontHeight - 8) / 2.0);
  }

  private BufferedImage glyphTexture(char c) {
    BufferedImage image = new BufferedImage(1, 1,
      BufferedImage.TYPE_INT_ARGB);
    Graphics2D graphics = (Graphics2D) image.getGraphics();
    applyGraphicSettings(graphics);
    FontMetrics metrics = graphics.getFontMetrics();

    Rectangle rect = metrics.getStringBounds(
      String.valueOf(c), graphics).getBounds();
    if (rect.getWidth() == 0.0
      || rect.getHeight() == 0.0) return null;

    graphics.dispose();

    image = new BufferedImage((int) rect.getWidth(),
      (int) rect.getHeight(), BufferedImage.TYPE_INT_ARGB);
    graphics = (Graphics2D) image.getGraphics();
    applyGraphicSettings(graphics);
    graphics.setColor(Color.white);
    metrics = graphics.getFontMetrics();

    graphics.drawString(String.valueOf(c), 1, 2 + metrics.getAscent());

    return image;
  }

  private void applyGraphicSettings(Graphics2D g) {
    g.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING,
      RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
      RenderingHints.VALUE_ANTIALIAS_ON);
    g.setRenderingHint(RenderingHints.KEY_RESOLUTION_VARIANT,
      RenderingHints.VALUE_RESOLUTION_VARIANT_DPI_FIT);
  }

  private void debugTexture(BufferedImage image, String fileName) {
    File file = Paths.get("").resolve("glyph_bin").toFile();
    if (!file.exists()) file.mkdir();

    file = new File(file, fileName);
    System.out.println(file);

    try {
      ImageIO.write(image, "png", file);
    } catch (IOException e) {
      e.printStackTrace();
    }
  }

  public Glyph glyph(char c) {
    if (c > GLYPH_BIN_SIZE) return null;
    return glyphs[c];
  }

  public int spaceWidth() {
    return spaceWidth;
  }

  public int fontHeight() {
    return fontHeight;
  }

  public Font font() {
    return font;
  }

  public DynamicTexture bitmapTexture() {
    return bitmapTexture;
  }
}
