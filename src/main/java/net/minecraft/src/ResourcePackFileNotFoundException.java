package net.minecraft.src;

import java.io.File;
import java.io.FileNotFoundException;

public class ResourcePackFileNotFoundException extends FileNotFoundException
{
    public ResourcePackFileNotFoundException(File par1File, String par2Str)
    {
        super(String.format("\'%s\' in ResourcePack \'%s\'", new Object[] {par2Str, par1File}));
    }
}
