package net.minecraft.src;

import java.util.Collection;
import java.util.UUID;

public interface AttributeInstance
{
    Attribute func_111123_a();

    double getBaseValue();

    void setAttribute(double var1);

    Collection func_111122_c();

    /**
     * Returns attribute modifier, if any, by the given UUID
     */
    AttributeModifier getModifier(UUID var1);

    void applyModifier(AttributeModifier var1);

    void removeModifier(AttributeModifier var1);

    void func_142049_d();

    double getAttributeValue();
}
