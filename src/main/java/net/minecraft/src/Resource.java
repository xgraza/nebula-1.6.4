package net.minecraft.src;

import java.io.InputStream;

public interface Resource
{
    InputStream getInputStream();

    boolean hasMetadata();

    MetadataSection getMetadata(String var1);
}
