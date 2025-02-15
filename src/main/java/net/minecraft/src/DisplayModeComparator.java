package net.minecraft.src;

import java.util.Comparator;
import org.lwjgl.opengl.DisplayMode;

public class DisplayModeComparator implements Comparator
{
    public int compare(Object o1, Object o2)
    {
        DisplayMode dm1 = (DisplayMode)o1;
        DisplayMode dm2 = (DisplayMode)o2;
        return dm1.getWidth() != dm2.getWidth() ? dm1.getWidth() - dm2.getWidth() : (dm1.getHeight() != dm2.getHeight() ? dm1.getHeight() - dm2.getHeight() : (dm1.getBitsPerPixel() != dm2.getBitsPerPixel() ? dm1.getBitsPerPixel() - dm2.getBitsPerPixel() : (dm1.getFrequency() != dm2.getFrequency() ? dm1.getFrequency() - dm2.getFrequency() : 0)));
    }
}
