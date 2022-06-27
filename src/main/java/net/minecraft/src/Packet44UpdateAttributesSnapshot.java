package net.minecraft.src;

import java.util.Collection;

public class Packet44UpdateAttributesSnapshot
{
    private final String field_142043_b;
    private final double field_142044_c;
    private final Collection field_142042_d;

    final Packet44UpdateAttributes field_142045_a;

    public Packet44UpdateAttributesSnapshot(Packet44UpdateAttributes par1Packet44UpdateAttributes, String par2Str, double par3, Collection par5Collection)
    {
        this.field_142045_a = par1Packet44UpdateAttributes;
        this.field_142043_b = par2Str;
        this.field_142044_c = par3;
        this.field_142042_d = par5Collection;
    }

    public String func_142040_a()
    {
        return this.field_142043_b;
    }

    public double func_142041_b()
    {
        return this.field_142044_c;
    }

    public Collection func_142039_c()
    {
        return this.field_142042_d;
    }
}
