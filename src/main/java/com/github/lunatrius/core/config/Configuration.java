//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.core.config;

import java.io.*;
import com.google.common.base.*;

public class Configuration
{
    private static final String FORMAT_NORMAL = "%1$s (default: %2$s)";
    private static final String FORMAT_RANGE = "%1$s (range: %2$s ~ %3$s, default: %4$s)";
    
    public Configuration(final File file) {
        //this.config = new net.minecraftforge.common.config.Configuration(file);
    }
    
    public void load() {
        //this.config.load();
    }
    
    public void save() {
        //this.config.save();
    }
    
    private String getDefaultListString(final String[] defaultValues) {
        return "[" + Joiner.on(", ").join((Object[])defaultValues) + "]";
    }
    
    private String getDefaultListString(final boolean[] defaultValues) {
        final String[] strings = new String[defaultValues.length];
        for (int i = 0; i < defaultValues.length; ++i) {
            strings[i] = String.valueOf(defaultValues[i]);
        }
        return this.getDefaultListString(strings);
    }
    
    private String getDefaultListString(final int[] defaultValues) {
        final String[] strings = new String[defaultValues.length];
        for (int i = 0; i < defaultValues.length; ++i) {
            strings[i] = String.valueOf(defaultValues[i]);
        }
        return this.getDefaultListString(strings);
    }
    
    private String getDefaultListString(final double[] defaultValues) {
        final String[] strings = new String[defaultValues.length];
        for (int i = 0; i < defaultValues.length; ++i) {
            strings[i] = String.valueOf(defaultValues[i]);
        }
        return this.getDefaultListString(strings);
    }
}
