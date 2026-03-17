package net.minecraft.client.resources;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreenWorking;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.renderer.texture.TextureManager;
import net.minecraft.client.resources.data.IMetadataSerializer;
import net.minecraft.client.resources.data.PackMetadataSection;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.util.EnumChatFormatting;
import net.minecraft.util.HttpUtil;
import net.minecraft.util.ResourceLocation;
import org.apache.commons.io.IOUtils;

import java.awt.image.BufferedImage;
import java.io.Closeable;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.*;

public class ResourcePackRepository
{
    protected static final FileFilter resourcePackFilter = (file) ->
    {
        boolean isZipFile = file.isFile() && file.getName().endsWith(".zip");
        boolean isDirAndHasMeta = file.isDirectory() && (new File(file, "pack.mcmeta")).isFile();
        return isZipFile || isDirAndHasMeta;
    };

    private final File dirResourcepacks;
    public final IResourcePack rprDefaultResourcePack;
    private final File field_148534_e;
    public final IMetadataSerializer rprMetadataSerializer;
    private IResourcePack downloadedResourcePack;
    private boolean downloading;
    private List<Entry> repositoryEntriesAll = Lists.newArrayList();
    private final List<Entry> repositoryEntries = Lists.newArrayList();

    public ResourcePackRepository(File resourcePackDir, File p_i45101_2_, IResourcePack p_i45101_3_, IMetadataSerializer metaSerializer, GameSettings settings)
    {
        this.dirResourcepacks = resourcePackDir;
        this.field_148534_e = p_i45101_2_;
        this.rprDefaultResourcePack = p_i45101_3_;
        this.rprMetadataSerializer = metaSerializer;
        this.fixDirResourcepacks();
        this.updateRepositoryEntriesAll();

        for (String resourcePack : settings.resourcePacks)
        {
            for (Entry entry : this.repositoryEntriesAll)
            {
                if (entry.getResourcePackName().equals(resourcePack))
                {
                    this.repositoryEntries.add(entry);
                    break;
                }
            }
        }
    }

    private void fixDirResourcepacks()
    {
        if (!this.dirResourcepacks.isDirectory())
        {
            this.dirResourcepacks.delete();
            this.dirResourcepacks.mkdirs();
        }
    }

    private List<File> getResourcePackFiles()
    {
        return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.emptyList();
    }

    public void updateRepositoryEntriesAll()
    {
        List<Entry> var1 = new ArrayList<>();
        Iterator<File> var2 = this.getResourcePackFiles().iterator();

        while (var2.hasNext())
        {
            File file = var2.next();
            ResourcePackRepository.Entry var4 = new ResourcePackRepository.Entry(file, null);

            if (!this.repositoryEntriesAll.contains(var4))
            {
                try
                {
                    var4.updateResourcePack();
                    var1.add(var4);
                } catch (Exception var6)
                {
                    var1.remove(var4);
                }
            } else
            {
                int var5 = this.repositoryEntriesAll.indexOf(var4);

                if (var5 > -1 && var5 < this.repositoryEntriesAll.size())
                {
                    var1.add(this.repositoryEntriesAll.get(var5));
                }
            }
        }

        this.repositoryEntriesAll.removeAll(var1);

        for (Entry entry : this.repositoryEntriesAll)
        {
            entry.closeResourcePack();
        }

        this.repositoryEntriesAll = var1;
    }

    public List<Entry> getRepositoryEntriesAll()
    {
        return ImmutableList.copyOf(this.repositoryEntriesAll);
    }

    public List<Entry> getRepositoryEntries()
    {
        return ImmutableList.copyOf(this.repositoryEntries);
    }

    public void addEntries(List<Entry> entries)
    {
        this.repositoryEntries.clear();
        this.repositoryEntries.addAll(entries);
    }

    public File getDirResourcepacks()
    {
        return this.dirResourcepacks;
    }

    public void func_148526_a(String p_148526_1_)
    {
        String var2 = p_148526_1_.substring(p_148526_1_.lastIndexOf("/") + 1);

        if (var2.contains("?"))
        {
            var2 = var2.substring(0, var2.indexOf("?"));
        }

        if (var2.endsWith(".zip"))
        {
            File var3 = new File(this.field_148534_e, var2.replaceAll("\\W", ""));
            this.resetDownloadedPack();
            this.func_148528_a(p_148526_1_, var3);
        }
    }

    private void func_148528_a(String downloadURL, File outputFile)
    {
        Minecraft mc = Minecraft.getMinecraft();
        GuiScreenWorking workingGui = new GuiScreenWorking();

        final HashMap<String, String> headerMap = Maps.newHashMap();
        headerMap.put("X-Minecraft-Username", mc.getSession().getUsername());
        headerMap.put("X-Minecraft-UUID", mc.getSession().getPlayerID());
        headerMap.put("X-Minecraft-Version", "1.7.2");

        downloading = true;
        mc.displayGuiScreen(workingGui);
        HttpUtil.downloadTexturePack(outputFile, downloadURL, (file) ->
        {
            if (downloading)
            {
                downloading = false;
                downloadedResourcePack = new FileResourcePack(file);
                mc.scheduleResourcesRefresh();
            }
        }, headerMap, 52428800, workingGui, mc.getProxy());
    }

    public IResourcePack func_148530_e()
    {
        return this.downloadedResourcePack;
    }

    public void resetDownloadedPack()
    {
        this.downloadedResourcePack = null;
        this.downloading = false;
    }

    public class Entry
    {
        private final File resourcePackFile;
        private IResourcePack reResourcePack;
        private PackMetadataSection rePackMetadataSection;
        private BufferedImage texturePackIcon;
        private ResourceLocation locationTexturePackIcon;

        private Entry(File par2File)
        {
            this.resourcePackFile = par2File;
        }

        public void updateResourcePack() throws IOException
        {
            this.reResourcePack = this.resourcePackFile.isDirectory() ? new FolderResourcePack(this.resourcePackFile) : new FileResourcePack(this.resourcePackFile);
            this.rePackMetadataSection = (PackMetadataSection) this.reResourcePack.getPackMetadata(ResourcePackRepository.this.rprMetadataSerializer, "pack");

            try
            {
                this.texturePackIcon = this.reResourcePack.getPackImage();
            } catch (IOException var2)
            {
            }

            if (this.texturePackIcon == null)
            {
                this.texturePackIcon = ResourcePackRepository.this.rprDefaultResourcePack.getPackImage();
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
                IOUtils.closeQuietly((Closeable) this.reResourcePack);
            }
        }

        public IResourcePack getResourcePack()
        {
            return this.reResourcePack;
        }

        public String getResourcePackName()
        {
            return this.reResourcePack.getPackName();
        }

        public String getTexturePackDescription()
        {
            return this.rePackMetadataSection == null ? EnumChatFormatting.RED + "Invalid pack.mcmeta (or missing 'pack' section)" : this.rePackMetadataSection.getPackDescription();
        }

        public boolean equals(Object par1Obj)
        {
            return this == par1Obj || (par1Obj instanceof Entry && this.toString().equals(par1Obj.toString()));
        }

        public int hashCode()
        {
            return this.toString().hashCode();
        }

        public String toString()
        {
            return String.format("%s:%s:%d", this.resourcePackFile.getName(), this.resourcePackFile.isDirectory() ? "folder" : "zip", Long.valueOf(this.resourcePackFile.lastModified()));
        }

        Entry(File par2File, Object par3ResourcePackRepositoryFilter)
        {
            this(par2File);
        }
    }
}
