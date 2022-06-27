package net.minecraft.src;

import java.io.IOException;

public interface TextureObject
{
    void loadTexture(ResourceManager var1) throws IOException;

    int getGlTextureId();
}
