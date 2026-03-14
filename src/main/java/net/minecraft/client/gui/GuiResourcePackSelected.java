package net.minecraft.client.gui;

import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.I18n;

import java.util.List;

public class GuiResourcePackSelected extends GuiResourcePackList
{
    private static final String __OBFID = "CL_00000827";

    public GuiResourcePackSelected(Minecraft p_i45056_1_, int p_i45056_2_, int p_i45056_3_, List p_i45056_4_)
    {
        super(p_i45056_1_, p_i45056_2_, p_i45056_3_, p_i45056_4_);
    }

    protected String func_148202_k()
    {
        return I18n.format("resourcePack.selected.title");
    }
}
