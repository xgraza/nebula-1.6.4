//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.world.schematic;

public class UnsupportedFormatException extends Exception
{
    public UnsupportedFormatException(final String format) {
        super(String.format("Unsupported format: %s", format));
    }
}
