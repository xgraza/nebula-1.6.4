package us.nebula.client.wdl;

import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;

/**
 * @author xgraza
 * @since 05/10/26
 */
public final class WorldDownloaderGUIScreen extends GuiScreen
{
    private final GuiScreen parent;

    public WorldDownloaderGUIScreen(final GuiScreen parent)
    {
        this.parent = parent;
    }

    @Override
    public void initGui()
    {
        super.initGui();

        buttonList.add(new GuiButton(0, width / 2 - 100,  height / 2 - 12, "Download once"));
        buttonList.add(new GuiButton(1, width / 2 - 100,  height / 2 + 10, "Continuous download"));
    }

    @Override
    public void drawScreen(int par1, int par2, float par3)
    {
        drawDefaultBackground();
        super.drawScreen(par1, par2, par3);

        drawCenteredString(mc.fontRenderer, "Nebula World Downloader", width / 2, height / 2 - 40, -1);
    }

    @Override
    protected void actionPerformed(GuiButton p_146284_1_)
    {
        if (p_146284_1_.id > 1)
        {
            return;
        }
        WorldDownloader.INSTANCE.setAlwaysDownload(p_146284_1_.id == 1);
        WorldDownloader.INSTANCE.start();
        mc.displayGuiScreen(parent);
    }
}
