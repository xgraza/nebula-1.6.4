package net.minecraft.client.resources;

import net.minecraft.client.resources.data.IMetadataSection;

import java.io.InputStream;

public interface IResource
{
    InputStream getInputStream();

    boolean hasMetadata();

    IMetadataSection getMetadata(String var1);
}
