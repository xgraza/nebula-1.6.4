package net.minecraft.src;

public class PackMetadataSection implements MetadataSection
{
    private final String packDescription;
    private final int packFormat;

    public PackMetadataSection(String par1Str, int par2)
    {
        this.packDescription = par1Str;
        this.packFormat = par2;
    }

    public String getPackDescription()
    {
        return this.packDescription;
    }

    public int getPackFormat()
    {
        return this.packFormat;
    }
}
