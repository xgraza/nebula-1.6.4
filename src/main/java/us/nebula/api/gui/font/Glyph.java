package us.nebula.api.gui.font;

public class Glyph
{
    private final char character;
    private final float x, y;
    private final double width, height;

    public Glyph(char character, float x, float y, double width, double height)
    {
        this.character = character;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
    }

    public char getCharacter()
    {
        return character;
    }

    public float getX()
    {
        return x;
    }

    public float getY()
    {
        return y;
    }

    public double getWidth()
    {
        return width;
    }

    public double getHeight()
    {
        return height;
    }
}
