package net.minecraft.src;

public interface Attribute
{
    String getAttributeUnlocalizedName();

    double clampValue(double var1);

    double getDefaultValue();

    boolean getShouldWatch();
}
