package net.minecraft.src;

class GuiBeaconButtonCancel extends GuiBeaconButton
{
    /** Beacon GUI this button belongs to. */
    final GuiBeacon beaconGui;

    public GuiBeaconButtonCancel(GuiBeacon par1GuiBeacon, int par2, int par3, int par4)
    {
        super(par2, par3, par4, GuiBeacon.getBeaconGuiTextures(), 112, 220);
        this.beaconGui = par1GuiBeacon;
    }

    public void func_82251_b(int par1, int par2)
    {
        this.beaconGui.drawCreativeTabHoveringText(I18n.getString("gui.cancel"), par1, par2);
    }
}
