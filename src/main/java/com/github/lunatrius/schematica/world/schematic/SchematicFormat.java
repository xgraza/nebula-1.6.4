//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.world.schematic;

import com.github.lunatrius.schematica.world.*;
import com.github.lunatrius.schematica.lib.*;

import java.nio.file.Files;
import java.util.zip.*;
import net.minecraft.nbt.*;
import java.io.*;
import java.util.*;

public abstract class SchematicFormat
{
    public static final Map<String, SchematicFormat> FORMATS;
    public static final String MATERIALS = "Materials";
    public static final String FORMAT_CLASSIC = "Classic";
    public static final String FORMAT_ALPHA = "Alpha";
    public static String FORMAT_DEFAULT;
    
    public abstract SchematicWorld readFromNBT(final NBTTagCompound p0);
    
    public abstract boolean writeToNBT(final NBTTagCompound p0, final SchematicWorld p1);
    
    public static SchematicWorld readFromFile(final File file) {
        try {
            final InputStream stream = Files.newInputStream(file.toPath());
            final NBTTagCompound tagCompound = CompressedStreamTools.readCompressed(stream);
            final String format = tagCompound.getString("Materials");
            final SchematicFormat schematicFormat = SchematicFormat.FORMATS.get(format);
            if (schematicFormat == null) {
                throw new UnsupportedFormatException(format);
            }
            return schematicFormat.readFromNBT(tagCompound);
        }
        catch (Exception ex) {
            Reference.logger.error("Failed to read schematic!", (Throwable)ex);
            return null;
        }
    }
    
    public static SchematicWorld readFromFile(final String directory, final String filename) {
        return readFromFile(new File(directory, filename));
    }
    
    public static boolean writeToFile(final File file, final SchematicWorld world) {
        try {
            final NBTTagCompound tagCompound = new NBTTagCompound();
            SchematicFormat.FORMATS.get(SchematicFormat.FORMAT_DEFAULT).writeToNBT(tagCompound, world);
            final DataOutputStream dataOutputStream = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(file)));
            try {
//                final Method method = ReflectionHelper.findMethod((Class)NBTTagCompound.class, (Object)null, new String[] { "func_150298_a", "a" }, new Class[] { String.class, NBTBase.class, DataOutput.class });
//                method.invoke(null, "Schematic", tagCompound, dataOutputStream);
                NBTTagCompound.func_150298_a("Schematic", tagCompound, dataOutputStream);
            }
            finally {
                dataOutputStream.close();
            }
            return true;
        }
        catch (Exception ex) {
            Reference.logger.error("Failed to write schematic!", (Throwable)ex);
            return false;
        }
    }
    
    public static boolean writeToFile(final File directory, final String filename, final SchematicWorld world) {
        return writeToFile(new File(directory, filename), world);
    }
    
    static {
        (FORMATS = new HashMap<String, SchematicFormat>()).put("Classic", new SchematicClassic());
        SchematicFormat.FORMATS.put("Alpha", (SchematicFormat)new SchematicAlpha());
        SchematicFormat.FORMAT_DEFAULT = "Alpha";
    }
}
