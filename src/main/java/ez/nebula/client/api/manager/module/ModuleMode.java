package ez.nebula.client.api.manager.module;

import ez.nebula.client.api.Togglable;
import net.minecraft.client.Minecraft;

public class ModuleMode<T extends Module> implements Togglable
{
    protected static final Minecraft MC = Minecraft.getMinecraft();
    protected final T parentModule;

    public ModuleMode(final T parentModule)
    {
        this.parentModule = parentModule;
    }

    @Override
    public void toggle()
    {

    }

    @Override
    public void setToggled(boolean state)
    {

    }

    @Override
    public boolean isToggled()
    {
        return false;
    }
}
