package net.minecraft.src;

public class ExceptionMcoService extends Exception
{
    public final int field_96392_a;
    public final String field_96391_b;
    public final int field_130097_c;

    public ExceptionMcoService(int par1, String par2Str, int par3)
    {
        super(par2Str);
        this.field_96392_a = par1;
        this.field_96391_b = par2Str;
        this.field_130097_c = par3;
    }

    public String toString()
    {
        return this.field_130097_c != -1 ? "Realms ( ErrorCode: " + this.field_130097_c + " )" : "Realms: " + this.field_96391_b;
    }
}
