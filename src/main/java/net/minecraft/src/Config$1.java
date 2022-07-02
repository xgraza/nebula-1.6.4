package net.minecraft.src;

import java.util.Comparator;
import org.lwjgl.opengl.DisplayMode;

final class Config$1 implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        DisplayMode dm1 = (DisplayMode)o1;
        DisplayMode dm2 = (DisplayMode)o2;
        return dm1.getWidth() != dm2.getWidth() ? dm2.getWidth() - dm1.getWidth() : (dm1.getHeight() != dm2.getHeight() ? dm2.getHeight() - dm1.getHeight() : 0);
    }
}
