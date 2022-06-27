package net.minecraft.src;

import java.util.concurrent.Callable;

class CallableScreenName implements Callable
{
    final EntityRenderer entityRender;

    CallableScreenName(EntityRenderer par1EntityRenderer)
    {
        this.entityRender = par1EntityRenderer;
    }

    public String callScreenName()
    {
        return EntityRenderer.getRendererMinecraft(this.entityRender).currentScreen.getClass().getCanonicalName();
    }

    public Object call()
    {
        return this.callScreenName();
    }
}
