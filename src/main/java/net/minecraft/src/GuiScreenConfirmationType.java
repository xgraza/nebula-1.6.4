package net.minecraft.src;

public enum GuiScreenConfirmationType
{
    Warning("Warning!", 16711680),
    Info("Info!", 8226750);
    public final int field_140075_c;
    public final String field_140072_d;

    private GuiScreenConfirmationType(String par3Str, int par4)
    {
        this.field_140072_d = par3Str;
        this.field_140075_c = par4;
    }
}
