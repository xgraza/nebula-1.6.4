package net.minecraft.src;

import java.util.concurrent.Callable;

class TextureManagerINNER1 implements Callable
{
    final TextureObject theTextureObject;

    final TextureManager theTextureManager;

    TextureManagerINNER1(TextureManager par1TextureManager, TextureObject par2TextureObject)
    {
        this.theTextureManager = par1TextureManager;
        this.theTextureObject = par2TextureObject;
    }

    public String func_135060_a()
    {
        return this.theTextureObject.getClass().getName();
    }

    public Object call()
    {
        return this.func_135060_a();
    }
}
