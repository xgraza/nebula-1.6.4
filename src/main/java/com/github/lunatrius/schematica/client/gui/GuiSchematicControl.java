//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.gui;

import com.github.lunatrius.schematica.world.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.*;
import com.github.lunatrius.schematica.*;
import com.github.lunatrius.core.util.vector.*;

public class GuiSchematicControl extends GuiScreen
{
    private final Settings settings;
    private final GuiScreen prevGuiScreen;
    private final SchematicWorld schematic;
    private final SchematicPrinter printer;
    private int centerX;
    private int centerY;
    private GuiButton btnDecX;
    private GuiButton btnAmountX;
    private GuiButton btnIncX;
    private GuiButton btnDecY;
    private GuiButton btnAmountY;
    private GuiButton btnIncY;
    private GuiButton btnDecZ;
    private GuiButton btnAmountZ;
    private GuiButton btnIncZ;
    private GuiButton btnDecLayer;
    private GuiButton btnIncLayer;
    private GuiButton btnHide;
    private GuiButton btnMove;
    private GuiButton btnFlip;
    private GuiButton btnRotate;
    private GuiButton btnMaterials;
    private GuiButton btnPrint;
    private int incrementX;
    private int incrementY;
    private int incrementZ;
    private final String strMoveSchematic;
    private final String strLayers;
    private final String strOperations;
    private final String strAll;
    private final String strX;
    private final String strY;
    private final String strZ;
    private final String strMaterials;
    private final String strPrinter;
    
    public GuiSchematicControl(final GuiScreen guiScreen) {
        this.settings = Settings.instance;
        this.centerX = 0;
        this.centerY = 0;
        this.btnDecX = null;
        this.btnAmountX = null;
        this.btnIncX = null;
        this.btnDecY = null;
        this.btnAmountY = null;
        this.btnIncY = null;
        this.btnDecZ = null;
        this.btnAmountZ = null;
        this.btnIncZ = null;
        this.btnDecLayer = null;
        this.btnIncLayer = null;
        this.btnHide = null;
        this.btnMove = null;
        this.btnFlip = null;
        this.btnRotate = null;
        this.btnMaterials = null;
        this.btnPrint = null;
        this.incrementX = 0;
        this.incrementY = 0;
        this.incrementZ = 0;
        this.strMoveSchematic = I18n.format("schematica.gui.moveschematic", new Object[0]);
        this.strLayers = I18n.format("schematica.gui.layers", new Object[0]);
        this.strOperations = I18n.format("schematica.gui.operations", new Object[0]);
        this.strAll = I18n.format("schematica.gui.all", new Object[0]);
        this.strX = I18n.format("schematica.gui.x", new Object[0]);
        this.strY = I18n.format("schematica.gui.y", new Object[0]);
        this.strZ = I18n.format("schematica.gui.z", new Object[0]);
        this.strMaterials = I18n.format("schematica.gui.materials", new Object[0]);
        this.strPrinter = I18n.format("schematica.gui.printer", new Object[0]);
        this.prevGuiScreen = guiScreen;
        this.schematic = Schematica.proxy.getActiveSchematic();
        this.printer = SchematicPrinter.INSTANCE;
    }
    
    public void initGui() {
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        this.buttonList.clear();
        int id = 0;
        this.btnDecX = new GuiButton(id++, this.centerX - 50, this.centerY - 30, 30, 20, I18n.format("schematica.gui.decrease", new Object[0]));
        this.buttonList.add(this.btnDecX);
        this.btnAmountX = new GuiButton(id++, this.centerX - 15, this.centerY - 30, 30, 20, Integer.toString(this.settings.increments[this.incrementX]));
        this.buttonList.add(this.btnAmountX);
        this.btnIncX = new GuiButton(id++, this.centerX + 20, this.centerY - 30, 30, 20, I18n.format("schematica.gui.increase", new Object[0]));
        this.buttonList.add(this.btnIncX);
        this.btnDecY = new GuiButton(id++, this.centerX - 50, this.centerY - 5, 30, 20, I18n.format("schematica.gui.decrease", new Object[0]));
        this.buttonList.add(this.btnDecY);
        this.btnAmountY = new GuiButton(id++, this.centerX - 15, this.centerY - 5, 30, 20, Integer.toString(this.settings.increments[this.incrementY]));
        this.buttonList.add(this.btnAmountY);
        this.btnIncY = new GuiButton(id++, this.centerX + 20, this.centerY - 5, 30, 20, I18n.format("schematica.gui.increase", new Object[0]));
        this.buttonList.add(this.btnIncY);
        this.btnDecZ = new GuiButton(id++, this.centerX - 50, this.centerY + 20, 30, 20, I18n.format("schematica.gui.decrease", new Object[0]));
        this.buttonList.add(this.btnDecZ);
        this.btnAmountZ = new GuiButton(id++, this.centerX - 15, this.centerY + 20, 30, 20, Integer.toString(this.settings.increments[this.incrementZ]));
        this.buttonList.add(this.btnAmountZ);
        this.btnIncZ = new GuiButton(id++, this.centerX + 20, this.centerY + 20, 30, 20, I18n.format("schematica.gui.increase", new Object[0]));
        this.buttonList.add(this.btnIncZ);
        this.btnDecLayer = new GuiButton(id++, this.width - 90, this.height - 150, 25, 20, I18n.format("schematica.gui.decrease", new Object[0]));
        this.buttonList.add(this.btnDecLayer);
        this.btnIncLayer = new GuiButton(id++, this.width - 35, this.height - 150, 25, 20, I18n.format("schematica.gui.increase", new Object[0]));
        this.buttonList.add(this.btnIncLayer);
        this.btnHide = new GuiButton(id++, this.width - 90, this.height - 105, 80, 20, I18n.format((this.schematic != null && this.schematic.isRendering()) ? "schematica.gui.hide" : "schematica.gui.show", new Object[0]));
        this.buttonList.add(this.btnHide);
        this.btnMove = new GuiButton(id++, this.width - 90, this.height - 80, 80, 20, I18n.format("schematica.gui.movehere", new Object[0]));
        this.buttonList.add(this.btnMove);
        this.btnFlip = new GuiButton(id++, this.width - 90, this.height - 55, 80, 20, I18n.format("schematica.gui.flip", new Object[0]));
        this.buttonList.add(this.btnFlip);
        this.btnRotate = new GuiButton(id++, this.width - 90, this.height - 30, 80, 20, I18n.format("schematica.gui.rotate", new Object[0]));
        this.buttonList.add(this.btnRotate);
        this.btnMaterials = new GuiButton(id++, 10, this.height - 70, 80, 20, I18n.format("schematica.gui.materials", new Object[0]));
        this.buttonList.add(this.btnMaterials);
        this.btnPrint = new GuiButton(id++, 10, this.height - 30, 80, 20, I18n.format(this.printer.isPrinting() ? "schematica.gui.disable" : "schematica.gui.enable", new Object[0]));
        this.buttonList.add(this.btnPrint);
        this.btnDecLayer.enabled = (this.schematic != null);
        this.btnIncLayer.enabled = (this.schematic != null);
        this.btnHide.enabled = (this.schematic != null);
        this.btnMove.enabled = (this.schematic != null);
        this.btnFlip.enabled = false;
        this.btnRotate.enabled = (this.schematic != null);
        this.btnMaterials.enabled = (this.schematic != null);
        this.btnPrint.enabled = (this.schematic != null && this.printer.isEnabled());
    }
    
    protected void actionPerformed(final GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton.id == this.btnDecX.id) {
                final Vector3f offset = this.settings.offset;
                offset.x -= this.settings.increments[this.incrementX];
            }
            else if (guiButton.id == this.btnIncX.id) {
                final Vector3f offset2 = this.settings.offset;
                offset2.x += this.settings.increments[this.incrementX];
            }
            else if (guiButton.id == this.btnAmountX.id) {
                this.incrementX = (this.incrementX + 1) % this.settings.increments.length;
                this.btnAmountX.displayString = Integer.toString(this.settings.increments[this.incrementX]);
            }
            else if (guiButton.id == this.btnDecY.id) {
                final Vector3f offset3 = this.settings.offset;
                offset3.y -= this.settings.increments[this.incrementY];
            }
            else if (guiButton.id == this.btnIncY.id) {
                final Vector3f offset4 = this.settings.offset;
                offset4.y += this.settings.increments[this.incrementY];
            }
            else if (guiButton.id == this.btnAmountY.id) {
                this.incrementY = (this.incrementY + 1) % this.settings.increments.length;
                this.btnAmountY.displayString = Integer.toString(this.settings.increments[this.incrementY]);
            }
            else if (guiButton.id == this.btnDecZ.id) {
                final Vector3f offset5 = this.settings.offset;
                offset5.z -= this.settings.increments[this.incrementZ];
            }
            else if (guiButton.id == this.btnIncZ.id) {
                final Vector3f offset6 = this.settings.offset;
                offset6.z += this.settings.increments[this.incrementZ];
            }
            else if (guiButton.id == this.btnAmountZ.id) {
                this.incrementZ = (this.incrementZ + 1) % this.settings.increments.length;
                this.btnAmountZ.displayString = Integer.toString(this.settings.increments[this.incrementZ]);
            }
            else if (guiButton.id == this.btnDecLayer.id) {
                if (this.schematic != null) {
                    this.schematic.decrementRenderingLayer();
                }
                this.settings.refreshSchematic();
            }
            else if (guiButton.id == this.btnIncLayer.id) {
                if (this.schematic != null) {
                    this.schematic.incrementRenderingLayer();
                }
                this.settings.refreshSchematic();
            }
            else if (guiButton.id == this.btnHide.id) {
                this.btnHide.displayString = I18n.format((this.schematic != null && this.schematic.toggleRendering()) ? "schematica.gui.hide" : "schematica.gui.show", new Object[0]);
            }
            else if (guiButton.id == this.btnMove.id) {
                this.settings.moveHere();
            }
            else if (guiButton.id == this.btnFlip.id) {
                if (this.schematic != null) {
                    this.schematic.flip();
                    this.settings.createRendererSchematicChunk();
                    SchematicPrinter.INSTANCE.refresh();
                }
            }
            else if (guiButton.id == this.btnRotate.id) {
                if (this.schematic != null) {
                    this.schematic.rotate();
                    this.settings.createRendererSchematicChunk();
                    SchematicPrinter.INSTANCE.refresh();
                }
            }
            else if (guiButton.id == this.btnMaterials.id) {
                this.mc.displayGuiScreen((GuiScreen)new GuiSchematicMaterials(this));
            }
            else if (guiButton.id == this.btnPrint.id && this.printer.isEnabled()) {
                final boolean isPrinting = this.printer.togglePrinting();
                this.btnPrint.displayString = I18n.format(isPrinting ? "schematica.gui.disable" : "schematica.gui.enable", new Object[0]);
            }
        }
    }
    
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.drawCenteredString(this.fontRendererObj, this.strMoveSchematic, this.centerX, this.centerY - 45, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.strMaterials, 50, this.height - 85, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.strPrinter, 50, this.height - 45, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.strLayers, this.width - 50, this.height - 165, 16777215);
        this.drawCenteredString(this.fontRendererObj, this.strOperations, this.width - 50, this.height - 120, 16777215);
        final int renderingLayer = (this.schematic != null) ? this.schematic.getRenderingLayer() : -1;
        this.drawCenteredString(this.fontRendererObj, (renderingLayer < 0) ? this.strAll : Integer.toString(renderingLayer + 1), this.width - 50, this.height - 145, 16777215);
        this.drawString(this.fontRendererObj, this.strX, this.centerX - 65, this.centerY - 24, 16777215);
        this.drawString(this.fontRendererObj, Integer.toString((int)this.settings.offset.x), this.centerX + 55, this.centerY - 24, 16777215);
        this.drawString(this.fontRendererObj, this.strY, this.centerX - 65, this.centerY + 1, 16777215);
        this.drawString(this.fontRendererObj, Integer.toString((int)this.settings.offset.y), this.centerX + 55, this.centerY + 1, 16777215);
        this.drawString(this.fontRendererObj, this.strZ, this.centerX - 65, this.centerY + 26, 16777215);
        this.drawString(this.fontRendererObj, Integer.toString((int)this.settings.offset.z), this.centerX + 55, this.centerY + 26, 16777215);
        super.drawScreen(par1, par2, par3);
    }
}
