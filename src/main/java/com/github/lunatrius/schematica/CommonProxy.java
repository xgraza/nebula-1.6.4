//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica;

import com.github.lunatrius.schematica.lib.*;
import java.io.*;
import net.minecraft.server.*;
import com.github.lunatrius.schematica.world.*;
import net.minecraft.entity.player.*;

public class CommonProxy
{
    public void registerKeybindings() {
    }
    
    public void registerEvents() {
    }
    
    public void createFolders() {
        Reference.schematicDirectory = Reference.config.schematicDirectory;
        if (!Reference.schematicDirectory.exists() && !Reference.schematicDirectory.mkdirs()) {
            Reference.logger.info("Could not create schematic directory [%s]!", new Object[] { Reference.schematicDirectory.getAbsolutePath() });
        }
    }
    
    public File getDataDirectory() {
        return MinecraftServer.getServer().getFile(".");
    }
    
    public void setActiveSchematic(final SchematicWorld world) {
    }
    
    public void setActiveSchematic(final SchematicWorld world, final EntityPlayer player) {
    }
    
    public SchematicWorld getActiveSchematic() {
        return null;
    }
    
    public SchematicWorld getActiveSchematic(final EntityPlayer player) {
        return null;
    }
}
