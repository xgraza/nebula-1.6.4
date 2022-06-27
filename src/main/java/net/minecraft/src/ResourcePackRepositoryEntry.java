package net.minecraft.src;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import org.apache.commons.io.IOUtils;

public class ResourcePackRepositoryEntry
{
    private final File resourcePackFile;
    private ResourcePack reResourcePack;
    private PackMetadataSection rePackMetadataSection;
    private BufferedImage texturePackIcon;
    private ResourceLocation locationTexturePackIcon;

    final ResourcePackRepository reResourcePackRepository;

    private ResourcePackRepositoryEntry(ResourcePackRepository par1ResourcePackRepository, File par2File)
    {
        this.reResourcePackRepository = par1ResourcePackRepository;
        this.resourcePackFile = par2File;
    }

    public void updateResourcePack() throws IOException
    {
        this.reResourcePack = (ResourcePack)(this.resourcePackFile.isDirectory() ? new FolderResourcePack(this.resourcePackFile) : new FileResourcePack(this.resourcePackFile));
        this.rePackMetadataSection = (PackMetadataSection)this.reResourcePack.getPackMetadata(this.reResourcePackRepository.rprMetadataSerializer, "pack");

        try
        {
            this.texturePackIcon = this.reResourcePack.getPackImage();
        }
        catch (IOException var2)
        {
            ;
        }

        if (this.texturePackIcon == null)
        {
            this.texturePackIcon = this.reResourcePackRepository.rprDefaultResourcePack.getPackImage();
        }

        this.closeResourcePack();
    }

    public void bindTexturePackIcon(TextureManager par1TextureManager)
    {
        if (this.locationTexturePackIcon == null)
        {
            this.locationTexturePackIcon = par1TextureManager.getDynamicTextureLocation("texturepackicon", new DynamicTexture(this.texturePackIcon));
        }

        par1TextureManager.bindTexture(this.locationTexturePackIcon);
    }

    public void closeResourcePack()
    {
        if (this.reResourcePack instanceof Closeable)
        {
            IOUtils.closeQuietly((Closeable)this.reResourcePack);
        }
    }

    public ResourcePack getResourcePack()
    {
        return this.reResourcePack;
    }

    public String getResourcePackName()
    {
        return this.reResourcePack.getPackName();
    }

    public String getTexturePackDescription()
    {
        return this.rePackMetadataSection == null ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing \'pack\' section)" : this.rePackMetadataSection.getPackDescription();
    }

    public boolean equals(Object par1Obj)
    {
        return this == par1Obj ? true : (par1Obj instanceof ResourcePackRepositoryEntry ? this.toString().equals(par1Obj.toString()) : false);
    }

    public int hashCode()
    {
        return this.toString().hashCode();
    }

    public String toString()
    {
        return String.format("%s:%s:%d", new Object[] {this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", Long.valueOf(this.resourcePackFile.lastModified())});
    }

    ResourcePackRepositoryEntry(ResourcePackRepository par1ResourcePackRepository, File par2File, ResourcePackRepositoryFilter par3ResourcePackRepositoryFilter)
    {
        this(par1ResourcePackRepository, par2File);
    }
}
