package net.minecraft.src;

import java.io.IOException;

final class TextureUtils$2 implements TickableTextureObject
{
    public void tick()
    {
        TextureAnimations.updateCustomAnimations();
    }

    public void loadTexture(ResourceManager var1) throws IOException {}

    public int getGlTextureId()
    {
        return 0;
    }
}
