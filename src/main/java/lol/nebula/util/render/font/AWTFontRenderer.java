package lol.nebula.util.render.font;

import lol.nebula.util.render.RenderUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.util.ResourceLocation;

import java.awt.*;

import static org.lwjgl.opengl.GL11.*;

public class AWTFontRenderer extends FontRenderer {
    protected static final Minecraft mc = Minecraft.getMinecraft();

    private final AWTFont normal, bold, italic, italicBold;
    private final Font font;

    public AWTFontRenderer(Font font) {
        super(mc.gameSettings, new ResourceLocation("textures/font/ascii.png"), mc.getTextureManager(), false);

        this.font = font;

        normal = new AWTFont(font);
        bold = new AWTFont(font.deriveFont(Font.BOLD));
        italic = new AWTFont(font.deriveFont(Font.ITALIC));
        italicBold = new AWTFont(font.deriveFont(Font.BOLD + Font.ITALIC));

        FONT_HEIGHT = normal.FONT_HEIGHT;
    }

    @Override
    public int drawString(String text, int x, int y, int color) {
        return drawString(text, (float) x, (float) y, color, false);
    }

    public int drawString(String text, float x, float y, int color) {
        return drawString(text, x, y, color, false);
    }

    public int drawString(String text, double x, double y, int color) {
        return drawString(text, (float) x, (float) y, color, false);
    }

    public int drawStringWithShadow(String text, float x, float y, int color) {
        float shadowWidth = drawString(text, x + 0.5f, y + 0.5f, color, true);
        return (int) Math.max(shadowWidth, drawString(text, x, y, color, false));
    }

    public float drawCenteredString(String text, float x, float y, int color) {
        return drawString(text, x - getStringWidth(text) / 2.0f, y, color);
    }

    public float drawCenteredString(String text, double x, double y, int color) {
        return drawString(text, x - getStringWidth(text) / 2.0, y, color);
    }

    public int drawString(String text, float x, float y, int color, boolean shadow) {
        if ((color & -67108864) == 0) {
            color |= -16777216;
        }

        if (shadow) {
            color = (color & 16579836) >> 2 | color & -16777216;
        }

        glPushMatrix();

        glEnable(GL_TEXTURE_2D);

        glScaled(0.5, 0.5, 0.5);
        x *= 2.0f;
        y *= 2.0f;

        glEnable(GL_BLEND);
        OpenGlHelper.glBlendFunc(770, 771, 1, 0);

        RenderUtils.setColor(color);

        glBindTexture(GL_TEXTURE_2D, normal.getTexture().getGlTextureId());

        glEnable(GL_POLYGON_SMOOTH);
        glHint(GL_POLYGON_SMOOTH_HINT, GL_NICEST);

        boolean boldFont = false, italicFont = false, strikethrough = false, underline = false;
        AWTFont awtFont = normal;

        for (int i = 0; i < text.length(); i++) {
            char c = text.charAt(i);
            if (c == '\u00a7') {
                int colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));

                float alpha = (color >> 24 & 0xFF) / 255.0F;
                float red = (color >> 16 & 0xff) / 255.0f;
                float green = (color >> 8 & 0xff) / 255.0f;
                float blue = (color & 0xff) / 255.0f;

                switch (colorIndex) {
                    case 17: {
                        boldFont = true;
                        awtFont = italicFont ? italicBold : bold;
                        glBindTexture(GL_TEXTURE_2D, awtFont.getTexture().getGlTextureId());
                        break;
                    }

                    case 18: {
                        strikethrough = true;
                        break;
                    }

                    case 19: {
                        underline = true;
                        break;
                    }

                    case 20: {
                        italicFont = true;
                        awtFont = boldFont ? italicBold : italic;
                        glBindTexture(GL_TEXTURE_2D, awtFont.getTexture().getGlTextureId());
                        break;
                    }

                    // reset
                    case 21: {
                        boldFont = false;
                        italicFont = false;
                        strikethrough = false;
                        underline = false;

                        glBindTexture(GL_TEXTURE_2D, (awtFont = normal).getTexture().getGlTextureId());
                        RenderUtils.setColor(red, green, blue, alpha);
                        break;
                    }

                    default: {
                        if (colorIndex < 16) { // 15 = white, anything else is "special"
                            boldFont = false;
                            italicFont = false;
                            strikethrough = false;
                            underline = false;

                            glBindTexture(GL_TEXTURE_2D, (awtFont = normal).getTexture().getGlTextureId());

                            if (shadow) {
                                colorIndex += 16;
                            }

                            int textFormattingColor = colorCode[colorIndex];
                            float r = (textFormattingColor >> 16 & 0xff) / 255.0f;
                            float g = (textFormattingColor >> 8 & 0xff) / 255.0f;
                            float b = (textFormattingColor & 0xff) / 255.0f;
                            RenderUtils.setColor(r, g, b, alpha);
                        }
                        break;
                    }
                }

                // next character
                ++i;
                continue;
            }

            CharData data = awtFont.getCharacter(c);
            if (data != null) {
                awtFont.drawChar(data, (int) x, (int) y);

                if (strikethrough) {
                    //RenderUtils.line(x, y + data.height / 2.0f, x + data.width - AWTFont.CHAR_OFFSET, y + data.height / 2.0f, 1.5f);
                }

                if (underline) {
                    //RenderUtils.line(x + data.width, y + (data.height - AWTFont.CHAR_OFFSET), x, y + (data.height - AWTFont.CHAR_OFFSET), 1.5f);
                }

                x += data.width - AWTFont.CHAR_OFFSET;
            }
        }

        glDisable(GL_POLYGON_SMOOTH);
        glPopMatrix();

        return (int) (x / 2.0f);
    }

    @Override
    public int getStringWidth(String text) {
        if (text == null || text.isEmpty()) {
            return 0;
        }

        float width = 0.0f;
        boolean boldFont = false, italicFont = false;

        CharData[] currentData = normal.getCharDataCache();
        for (int i = 0; i < text.length(); i++) {
            char character = text.charAt(i);
            if (character == '\u00a7') {
                int colorIndex = "0123456789abcdefklmnor".indexOf(text.charAt(i + 1));

                switch (colorIndex) {
                    case 17: {
                        boldFont = true;
                        currentData = (italicFont ? italicBold : bold).getCharDataCache();
                        break;
                    }

                    case 20: {
                        italicFont = true;
                        currentData = (boldFont ? italicBold : italic).getCharDataCache();
                        break;
                    }

                    case 21: {
                        boldFont = false;
                        italicFont = false;
                        currentData = normal.getCharDataCache();
                        break;
                    }

                    default: {
                        boldFont = false;
                        italicFont = false;
                        break;
                    }
                }

                i++;
                continue;
            }

            if (character < currentData.length) {
                width += currentData[character].width - AWTFont.CHAR_OFFSET;
            }
        }

        return (int) (width / 2.0f);
    }

    public Font getFont() {
        return font;
    }
}
