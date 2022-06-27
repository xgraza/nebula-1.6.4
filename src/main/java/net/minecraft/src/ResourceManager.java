package net.minecraft.src;

import java.io.IOException;
import java.util.List;
import java.util.Set;

public interface ResourceManager
{
    Set getResourceDomains();

    Resource getResource(ResourceLocation var1) throws IOException;

    List getAllResources(ResourceLocation var1) throws IOException;
}
