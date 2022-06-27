package net.minecraft.src;

public class TextureMetadataSection implements MetadataSection
{
    private final boolean textureBlur;
    private final boolean textureClamp;

    public TextureMetadataSection(boolean par1, boolean par2)
    {
        this.textureBlur = par1;
        this.textureClamp = par2;
    }

    public boolean getTextureBlur()
    {
        return this.textureBlur;
    }

    public boolean getTextureClamp()
    {
        return this.textureClamp;
    }
}
