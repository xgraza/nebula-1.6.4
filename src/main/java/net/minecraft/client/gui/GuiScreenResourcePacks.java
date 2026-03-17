package net.minecraft.client.gui;

import com.google.common.collect.Lists;
import net.minecraft.client.resources.*;
import net.minecraft.util.Util;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.lwjgl.Sys;
import us.nebula.client.impl.gui.loading.LoadingScreen;

import java.awt.Desktop;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class GuiScreenResourcePacks extends GuiScreen
{
    private static final Logger LOGGER = LogManager.getLogger();
    private final GuiScreen parent;

    private final List<ResourcePackListEntry> foundResourcePackEntryList = new ArrayList<>();
    private final List<ResourcePackListEntry> field_146969_h = new ArrayList<>();

    private GuiResourcePackAvailable availableResourcePacksGui;
    private GuiResourcePackSelected selectedResourcePacksGui;

    public GuiScreenResourcePacks(GuiScreen parent)
    {
        this.parent = parent;
    }

    /**
     * Adds the buttons (and other controls) to the screen in question.
     */
    public void initGui()
    {
        buttonList.add(new GuiOptionButton(2, width / 2 - 154, height - 48, I18n.format("resourcePack.openFolder")));
        buttonList.add(new GuiOptionButton(1, width / 2 + 4, height - 48, I18n.format("gui.done")));

        final ResourcePackRepository packRepo = mc.getResourcePackRepository();
        packRepo.updateRepositoryEntriesAll();

        List<ResourcePackRepository.Entry> entryList = packRepo.getRepositoryEntriesAll();
        entryList.removeAll(packRepo.getRepositoryEntries());
        for (ResourcePackRepository.Entry entry : entryList)
        {
            foundResourcePackEntryList.add(new ResourcePackListEntryFound(this, entry));
        }

        entryList = Lists.reverse(packRepo.getRepositoryEntries());
        for (ResourcePackRepository.Entry entry : entryList)
        {
            field_146969_h.add(new ResourcePackListEntryFound(this, entry));
        }

        field_146969_h.add(new ResourcePackListEntryDefault(this));
        availableResourcePacksGui = new GuiResourcePackAvailable(mc, 200, height, foundResourcePackEntryList);
        availableResourcePacksGui.func_148140_g(width / 2 - 4 - 200);
        availableResourcePacksGui.registerScrollButtons(7, 8);
        selectedResourcePacksGui = new GuiResourcePackSelected(mc, 200, height, field_146969_h);
        selectedResourcePacksGui.func_148140_g(width / 2 + 4);
        selectedResourcePacksGui.registerScrollButtons(7, 8);
    }

    public boolean func_146961_a(ResourcePackListEntry entry)
    {
        return field_146969_h.contains(entry);
    }

    public List func_146962_b(ResourcePackListEntry entry)
    {
        return func_146961_a(entry) ? field_146969_h : foundResourcePackEntryList;
    }

    public List func_146964_g()
    {
        return foundResourcePackEntryList;
    }

    public List func_146963_h()
    {
        return field_146969_h;
    }

    protected void actionPerformed(GuiButton button)
    {
        if (button.enabled)
        {
            if (button.id == 2)
            {
                File file = mc.getResourcePackRepository().getDirResourcepacks();
                String path = file.getAbsolutePath();

                if (Util.getOSType() == Util.EnumOS.MACOS)
                {
                    try
                    {
                        LOGGER.info(path);
                        Runtime.getRuntime().exec(new String[]{ "/usr/bin/open", path });
                        return;
                    } catch (IOException var9)
                    {
                        LOGGER.error("Couldn't open file", var9);
                    }
                } else if (Util.getOSType() == Util.EnumOS.WINDOWS)
                {
                    String var4 = String.format("cmd.exe /C start \"Open file\" \"%s\"", path);

                    try
                    {
                        Runtime.getRuntime().exec(var4);
                        return;
                    } catch (IOException var8)
                    {
                        LOGGER.error("Couldn't open file", var8);
                    }
                }

                boolean opened = false;

                try
                {
                    Desktop.getDesktop().browse(file.toURI());
                } catch (Throwable var7)
                {
                    LOGGER.error("Couldn't open link", var7);
                    opened = true;
                }

                if (opened)
                {
                    LOGGER.info("Opening via system class!");
                    Sys.openURL("file://" + path);
                }
            } else if (button.id == 1)
            {
                ArrayList<ResourcePackRepository.Entry> entryList = Lists.newArrayList();

                for (ResourcePackListEntry entry : field_146969_h)
                {
                    if (entry instanceof ResourcePackListEntryFound)
                    {
                        entryList.add(((ResourcePackListEntryFound) entry).func_148318_i());
                    }
                }

                Collections.reverse(entryList);
                mc.getResourcePackRepository().addEntries(entryList);
                mc.gameSettings.resourcePacks.clear();

                for (ResourcePackRepository.Entry entry : entryList)
                {
                    mc.gameSettings.resourcePacks.add(entry.getResourcePackName());
                }

                mc.gameSettings.saveOptions();
                mc.refreshResources();
                mc.displayGuiScreen(parent);
                LoadingScreen.setTotalLoadingStages(0);
            }
        }
    }

    /**
     * Called when the mouse is clicked.
     */
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton)
    {
        super.mouseClicked(mouseX, mouseY, mouseButton);
        availableResourcePacksGui.func_148179_a(mouseX, mouseY, mouseButton);
        selectedResourcePacksGui.func_148179_a(mouseX, mouseY, mouseButton);
    }

    protected void mouseMovedOrUp(int p_146286_1_, int p_146286_2_, int p_146286_3_)
    {
        super.mouseMovedOrUp(p_146286_1_, p_146286_2_, p_146286_3_);
    }

    /**
     * Draws the screen and all the components in it.
     */
    public void drawScreen(int par1, int par2, float par3)
    {
        drawBackground(0);
        availableResourcePacksGui.drawScreen(par1, par2, par3);
        selectedResourcePacksGui.drawScreen(par1, par2, par3);
        drawCenteredString(fontRenderer, I18n.format("resourcePack.title"), width / 2, 16, 16777215);
        drawCenteredString(fontRenderer, I18n.format("resourcePack.folderInfo"), width / 2 - 77, height - 26, 8421504);
        super.drawScreen(par1, par2, par3);
    }
}
