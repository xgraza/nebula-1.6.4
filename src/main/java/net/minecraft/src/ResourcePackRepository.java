package net.minecraft.src;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Iterator;
import java.util.List;

public class ResourcePackRepository
{
    protected static final FileFilter resourcePackFilter = new ResourcePackRepositoryFilter();
    private final File dirResourcepacks;
    public final ResourcePack rprDefaultResourcePack;
    public final MetadataSerializer rprMetadataSerializer;
    private List repositoryEntriesAll = Lists.newArrayList();
    private List repositoryEntries = Lists.newArrayList();

    public ResourcePackRepository(File par1File, ResourcePack par2ResourcePack, MetadataSerializer par3MetadataSerializer, GameSettings par4GameSettings)
    {
        this.dirResourcepacks = par1File;
        this.rprDefaultResourcePack = par2ResourcePack;
        this.rprMetadataSerializer = par3MetadataSerializer;
        this.fixDirResourcepacks();
        this.updateRepositoryEntriesAll();
        Iterator var5 = this.repositoryEntriesAll.iterator();

        while (var5.hasNext())
        {
            ResourcePackRepositoryEntry var6 = (ResourcePackRepositoryEntry)var5.next();

            if (var6.getResourcePackName().equals(par4GameSettings.skin))
            {
                this.repositoryEntries.add(var6);
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

    private List getResourcePackFiles()
    {
        return this.dirResourcepacks.isDirectory() ? Arrays.asList(this.dirResourcepacks.listFiles(resourcePackFilter)) : Collections.emptyList();
    }

    public void updateRepositoryEntriesAll()
    {
        ArrayList var1 = Lists.newArrayList();
        Iterator var2 = this.getResourcePackFiles().iterator();

        while (var2.hasNext())
        {
            File var3 = (File)var2.next();
            ResourcePackRepositoryEntry var4 = new ResourcePackRepositoryEntry(this, var3, (ResourcePackRepositoryFilter)null);

            if (!this.repositoryEntriesAll.contains(var4))
            {
                try
                {
                    var4.updateResourcePack();
                    var1.add(var4);
                }
                catch (Exception var6)
                {
                    var1.remove(var4);
                }
            }
            else
            {
                var1.add(this.repositoryEntriesAll.get(this.repositoryEntriesAll.indexOf(var4)));
            }
        }

        this.repositoryEntriesAll.removeAll(var1);
        var2 = this.repositoryEntriesAll.iterator();

        while (var2.hasNext())
        {
            ResourcePackRepositoryEntry var7 = (ResourcePackRepositoryEntry)var2.next();
            var7.closeResourcePack();
        }

        this.repositoryEntriesAll = var1;
    }

    public List getRepositoryEntriesAll()
    {
        return ImmutableList.copyOf(this.repositoryEntriesAll);
    }

    public List getRepositoryEntries()
    {
        return ImmutableList.copyOf(this.repositoryEntries);
    }

    public String getResourcePackName()
    {
        return this.repositoryEntries.isEmpty() ? "Default" : ((ResourcePackRepositoryEntry)this.repositoryEntries.get(0)).getResourcePackName();
    }

    public void setRepositoryEntries(ResourcePackRepositoryEntry ... par1ArrayOfResourcePackRepositoryEntry)
    {
        this.repositoryEntries.clear();
        Collections.addAll(this.repositoryEntries, par1ArrayOfResourcePackRepositoryEntry);
    }

    public File getDirResourcepacks()
    {
        return this.dirResourcepacks;
    }
}
