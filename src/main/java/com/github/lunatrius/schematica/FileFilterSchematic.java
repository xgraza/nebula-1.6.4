//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica;

import java.io.*;

public class FileFilterSchematic implements FileFilter
{
    private final boolean directory;
    
    public FileFilterSchematic(final boolean dir) {
        this.directory = dir;
    }
    
    @Override
    public boolean accept(final File file) {
        if (this.directory) {
            return file.isDirectory();
        }
        return file.getName().toLowerCase().endsWith(".schematic");
    }
}
