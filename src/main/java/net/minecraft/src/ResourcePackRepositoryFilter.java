package net.minecraft.src;

import java.io.File;
import java.io.FileFilter;

final class ResourcePackRepositoryFilter implements FileFilter
{
    public boolean accept(File par1File)
    {
        boolean var2 = par1File.isFile() && par1File.getName().endsWith(".zip");
        boolean var3 = par1File.isDirectory() && (new File(par1File, "pack.mcmeta")).isFile();
        return var2 || var3;
    }
}
