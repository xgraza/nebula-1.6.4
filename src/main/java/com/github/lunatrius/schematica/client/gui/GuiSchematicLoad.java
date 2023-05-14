//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.gui;

import com.github.lunatrius.schematica.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.*;
import com.github.lunatrius.schematica.lib.*;
import java.util.*;
import java.net.*;
import org.lwjgl.*;
import java.io.*;
import net.minecraft.init.*;
import com.github.lunatrius.schematica.world.*;
import net.minecraft.item.*;

public class GuiSchematicLoad extends GuiScreen
{
    private static final FileFilterSchematic FILE_FILTER_FOLDER;
    private static final FileFilterSchematic FILE_FILTER_SCHEMATIC;
    private final Settings settings;
    private final GuiScreen prevGuiScreen;
    private GuiSchematicLoadSlot guiSchematicLoadSlot;
    private GuiButton btnOpenDir;
    private GuiButton btnDone;
    private final String strTitle;
    private final String strFolderInfo;
    protected File currentDirectory;
    protected final List<GuiSchematicEntry> schematicFiles;
    
    public GuiSchematicLoad(final GuiScreen guiScreen) {
        this.settings = Settings.instance;
        this.btnOpenDir = null;
        this.btnDone = null;
        this.strTitle = I18n.format("schematica.gui.title", new Object[0]);
        this.strFolderInfo = I18n.format("schematica.gui.folderInfo", new Object[0]);
        this.currentDirectory = Reference.schematicDirectory;
        this.schematicFiles = new ArrayList<GuiSchematicEntry>();
        this.prevGuiScreen = guiScreen;
    }
    
    public void initGui() {
        int id = 0;
        this.btnOpenDir = new GuiButton(id++, this.width / 2 - 154, this.height - 36, 150, 20, I18n.format("schematica.gui.openFolder", new Object[0]));
        this.buttonList.add(this.btnOpenDir);
        this.btnDone = new GuiButton(id++, this.width / 2 + 4, this.height - 36, 150, 20, I18n.format("schematica.gui.done", new Object[0]));
        this.buttonList.add(this.btnDone);
        this.guiSchematicLoadSlot = new GuiSchematicLoadSlot(this);
        this.reloadSchematics();
    }
    
    protected void actionPerformed(final GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton.id == this.btnOpenDir.id) {
                boolean success = false;
                try {
                    final Class c = Class.forName("java.awt.Desktop");
                    final Object m = c.getMethod("getDesktop", (Class[])new Class[0]).invoke(null, new Object[0]);
                    c.getMethod("browse", URI.class).invoke(m, Reference.schematicDirectory.toURI());
                }
                catch (Throwable e) {
                    success = true;
                }
                if (success) {
                    Reference.logger.info("Opening via Sys class!");
                    Sys.openURL("file://" + Reference.schematicDirectory.getAbsolutePath());
                }
            }
            else if (guiButton.id == this.btnDone.id) {
                if (this.settings.isLoadEnabled) {
                    this.loadSchematic();
                }
                this.mc.displayGuiScreen(this.prevGuiScreen);
            }
            else {
                this.guiSchematicLoadSlot.func_148147_a(guiButton);
            }
        }
    }
    
    public void drawScreen(final int x, final int y, final float partialTicks) {
        this.guiSchematicLoadSlot.func_148128_a(x, y, partialTicks);
        this.drawCenteredString(this.fontRendererObj, this.strTitle, this.width / 2, 4, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.strFolderInfo, this.width / 2 - 78, this.height - 12, 8421504);
        super.drawScreen(x, y, partialTicks);
    }
    
    public void onGuiClosed() {
    }
    
    protected void changeDirectory(final String directory) {
        this.currentDirectory = new File(this.currentDirectory, directory);
        this.reloadSchematics();
    }
    
    protected void reloadSchematics() {
        String name = null;
        Item item = null;
        this.schematicFiles.clear();
        try {
            if (!this.currentDirectory.getCanonicalPath().equals(Reference.schematicDirectory.getCanonicalPath())) {
                this.schematicFiles.add(new GuiSchematicEntry("..", Items.lava_bucket, 0, true));
            }
        }
        catch (IOException e) {
            Reference.logger.error("Failed to add GuiSchematicEntry!", (Throwable)e);
        }
        final File[] filesFolders = this.currentDirectory.listFiles(GuiSchematicLoad.FILE_FILTER_FOLDER);
        if (filesFolders == null) {
            Reference.logger.error(String.format("listFiles returned null (directory: %s)!", this.currentDirectory));
        }
        else {
            for (final File file : filesFolders) {
                if (file != null) {
                    name = file.getName();
                    final File[] files = file.listFiles();
                    item = ((files == null || files.length == 0) ? Items.bucket : Items.water_bucket);
                    this.schematicFiles.add(new GuiSchematicEntry(name, item, 0, file.isDirectory()));
                }
            }
        }
        final File[] filesSchematics = this.currentDirectory.listFiles(GuiSchematicLoad.FILE_FILTER_SCHEMATIC);
        if (filesSchematics == null || filesSchematics.length == 0) {
            this.schematicFiles.add(new GuiSchematicEntry(I18n.format("schematica.gui.noschematic", new Object[0]), Blocks.dirt, 0, false));
        }
        else {
            for (final File file2 : filesSchematics) {
                name = file2.getName();
                this.schematicFiles.add(new GuiSchematicEntry(name, SchematicWorld.getIconFromFile(file2), file2.isDirectory()));
            }
        }
    }
    
    private void loadSchematic() {
        final int selectedIndex = this.guiSchematicLoadSlot.selectedIndex;
        try {
            if (selectedIndex >= 0 && selectedIndex < this.schematicFiles.size()) {
                final GuiSchematicEntry schematic = this.schematicFiles.get(selectedIndex);
                this.settings.loadSchematic(new File(this.currentDirectory, schematic.getName()).getCanonicalPath());
            }
        }
        catch (Exception e) {
            Reference.logger.error("Failed to load schematic!", (Throwable)e);
        }
        this.settings.moveHere();
    }
    
    static {
        FILE_FILTER_FOLDER = new FileFilterSchematic(true);
        FILE_FILTER_SCHEMATIC = new FileFilterSchematic(false);
    }
}
