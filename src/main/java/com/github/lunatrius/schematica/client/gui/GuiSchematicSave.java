//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.gui;

import com.github.lunatrius.schematica.*;
import net.minecraft.client.gui.*;
import net.minecraft.client.resources.*;
import com.github.lunatrius.schematica.lib.*;
import com.github.lunatrius.core.util.vector.*;

public class GuiSchematicSave extends GuiScreen
{
    private final Settings settings;
    private final GuiScreen prevGuiScreen;
    private int centerX;
    private int centerY;
    private GuiButton btnPointA;
    private GuiButton btnDecAX;
    private GuiButton btnAmountAX;
    private GuiButton btnIncAX;
    private GuiButton btnDecAY;
    private GuiButton btnAmountAY;
    private GuiButton btnIncAY;
    private GuiButton btnDecAZ;
    private GuiButton btnAmountAZ;
    private GuiButton btnIncAZ;
    private GuiButton btnPointB;
    private GuiButton btnDecBX;
    private GuiButton btnAmountBX;
    private GuiButton btnIncBX;
    private GuiButton btnDecBY;
    private GuiButton btnAmountBY;
    private GuiButton btnIncBY;
    private GuiButton btnDecBZ;
    private GuiButton btnAmountBZ;
    private GuiButton btnIncBZ;
    private int incrementAX;
    private int incrementAY;
    private int incrementAZ;
    private int incrementBX;
    private int incrementBY;
    private int incrementBZ;
    private GuiButton btnEnable;
    private GuiButton btnSave;
    private GuiTextField tfFilename;
    private String filename;
    private final String strSaveSelection;
    private final String strX;
    private final String strY;
    private final String strZ;
    
    public GuiSchematicSave(final GuiScreen guiScreen) {
        this.settings = Settings.instance;
        this.centerX = 0;
        this.centerY = 0;
        this.btnPointA = null;
        this.btnDecAX = null;
        this.btnAmountAX = null;
        this.btnIncAX = null;
        this.btnDecAY = null;
        this.btnAmountAY = null;
        this.btnIncAY = null;
        this.btnDecAZ = null;
        this.btnAmountAZ = null;
        this.btnIncAZ = null;
        this.btnPointB = null;
        this.btnDecBX = null;
        this.btnAmountBX = null;
        this.btnIncBX = null;
        this.btnDecBY = null;
        this.btnAmountBY = null;
        this.btnIncBY = null;
        this.btnDecBZ = null;
        this.btnAmountBZ = null;
        this.btnIncBZ = null;
        this.incrementAX = 0;
        this.incrementAY = 0;
        this.incrementAZ = 0;
        this.incrementBX = 0;
        this.incrementBY = 0;
        this.incrementBZ = 0;
        this.btnEnable = null;
        this.btnSave = null;
        this.tfFilename = null;
        this.filename = "";
        this.strSaveSelection = I18n.format("schematica.gui.saveselection", new Object[0]);
        this.strX = I18n.format("schematica.gui.x", new Object[0]);
        this.strY = I18n.format("schematica.gui.y", new Object[0]);
        this.strZ = I18n.format("schematica.gui.z", new Object[0]);
        this.prevGuiScreen = guiScreen;
    }
    
    public void initGui() {
        this.centerX = this.width / 2;
        this.centerY = this.height / 2;
        this.buttonList.clear();
        int id = 0;
        this.btnPointA = new GuiButton(id++, this.centerX - 130, this.centerY - 55, 100, 20, I18n.format("schematica.gui.point.red", new Object[0]));
        this.buttonList.add(this.btnPointA);
        this.btnDecAX = new GuiButton(id++, this.centerX - 130, this.centerY - 30, 30, 20, I18n.format("schematica.gui.decrease", new Object[0]));
        this.buttonList.add(this.btnDecAX);
        this.btnAmountAX = new GuiButton(id++, this.centerX - 95, this.centerY - 30, 30, 20, Integer.toString(this.settings.increments[this.incrementAX]));
        this.buttonList.add(this.btnAmountAX);
        this.btnIncAX = new GuiButton(id++, this.centerX - 60, this.centerY - 30, 30, 20, I18n.format("schematica.gui.increase", new Object[0]));
        this.buttonList.add(this.btnIncAX);
        this.btnDecAY = new GuiButton(id++, this.centerX - 130, this.centerY - 5, 30, 20, I18n.format("schematica.gui.decrease", new Object[0]));
        this.buttonList.add(this.btnDecAY);
        this.btnAmountAY = new GuiButton(id++, this.centerX - 95, this.centerY - 5, 30, 20, Integer.toString(this.settings.increments[this.incrementAY]));
        this.buttonList.add(this.btnAmountAY);
        this.btnIncAY = new GuiButton(id++, this.centerX - 60, this.centerY - 5, 30, 20, I18n.format("schematica.gui.increase", new Object[0]));
        this.buttonList.add(this.btnIncAY);
        this.btnDecAZ = new GuiButton(id++, this.centerX - 130, this.centerY + 20, 30, 20, I18n.format("schematica.gui.decrease", new Object[0]));
        this.buttonList.add(this.btnDecAZ);
        this.btnAmountAZ = new GuiButton(id++, this.centerX - 95, this.centerY + 20, 30, 20, Integer.toString(this.settings.increments[this.incrementAZ]));
        this.buttonList.add(this.btnAmountAZ);
        this.btnIncAZ = new GuiButton(id++, this.centerX - 60, this.centerY + 20, 30, 20, I18n.format("schematica.gui.increase", new Object[0]));
        this.buttonList.add(this.btnIncAZ);
        this.btnPointB = new GuiButton(id++, this.centerX + 30, this.centerY - 55, 100, 20, I18n.format("schematica.gui.point.blue", new Object[0]));
        this.buttonList.add(this.btnPointB);
        this.btnDecBX = new GuiButton(id++, this.centerX + 30, this.centerY - 30, 30, 20, I18n.format("schematica.gui.decrease", new Object[0]));
        this.buttonList.add(this.btnDecBX);
        this.btnAmountBX = new GuiButton(id++, this.centerX + 65, this.centerY - 30, 30, 20, Integer.toString(this.settings.increments[this.incrementBX]));
        this.buttonList.add(this.btnAmountBX);
        this.btnIncBX = new GuiButton(id++, this.centerX + 100, this.centerY - 30, 30, 20, I18n.format("schematica.gui.increase", new Object[0]));
        this.buttonList.add(this.btnIncBX);
        this.btnDecBY = new GuiButton(id++, this.centerX + 30, this.centerY - 5, 30, 20, I18n.format("schematica.gui.decrease", new Object[0]));
        this.buttonList.add(this.btnDecBY);
        this.btnAmountBY = new GuiButton(id++, this.centerX + 65, this.centerY - 5, 30, 20, Integer.toString(this.settings.increments[this.incrementBY]));
        this.buttonList.add(this.btnAmountBY);
        this.btnIncBY = new GuiButton(id++, this.centerX + 100, this.centerY - 5, 30, 20, I18n.format("schematica.gui.increase", new Object[0]));
        this.buttonList.add(this.btnIncBY);
        this.btnDecBZ = new GuiButton(id++, this.centerX + 30, this.centerY + 20, 30, 20, I18n.format("schematica.gui.decrease", new Object[0]));
        this.buttonList.add(this.btnDecBZ);
        this.btnAmountBZ = new GuiButton(id++, this.centerX + 65, this.centerY + 20, 30, 20, Integer.toString(this.settings.increments[this.incrementBZ]));
        this.buttonList.add(this.btnAmountBZ);
        this.btnIncBZ = new GuiButton(id++, this.centerX + 100, this.centerY + 20, 30, 20, I18n.format("schematica.gui.increase", new Object[0]));
        this.buttonList.add(this.btnIncBZ);
        this.btnEnable = new GuiButton(id++, this.width - 210, this.height - 30, 50, 20, I18n.format(this.settings.isRenderingGuide ? "schematica.gui.disable" : "schematica.gui.enable", new Object[0]));
        this.buttonList.add(this.btnEnable);
        this.tfFilename = new GuiTextField(this.fontRendererObj, this.width - 155, this.height - 29, 100, 18);
        this.btnSave = new GuiButton(id++, this.width - 50, this.height - 30, 40, 20, I18n.format("schematica.gui.save", new Object[0]));
        this.btnSave.enabled = this.settings.isRenderingGuide;
        this.buttonList.add(this.btnSave);
        this.tfFilename.func_146203_f(1024);
        this.tfFilename.setText(this.filename);
    }
    
    protected void actionPerformed(final GuiButton guiButton) {
        if (guiButton.enabled) {
            if (guiButton.id == this.btnPointA.id) {
                this.settings.moveHere(this.settings.pointA);
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnDecAX.id) {
                final Vector3f pointA = this.settings.pointA;
                pointA.x -= this.settings.increments[this.incrementAX];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnIncAX.id) {
                final Vector3f pointA2 = this.settings.pointA;
                pointA2.x += this.settings.increments[this.incrementAX];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnAmountAX.id) {
                this.incrementAX = (this.incrementAX + 1) % this.settings.increments.length;
                this.btnAmountAX.displayString = Integer.toString(this.settings.increments[this.incrementAX]);
            }
            else if (guiButton.id == this.btnDecAY.id) {
                final Vector3f pointA3 = this.settings.pointA;
                pointA3.y -= this.settings.increments[this.incrementAY];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnIncAY.id) {
                final Vector3f pointA4 = this.settings.pointA;
                pointA4.y += this.settings.increments[this.incrementAY];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnAmountAY.id) {
                this.incrementAY = (this.incrementAY + 1) % this.settings.increments.length;
                this.btnAmountAY.displayString = Integer.toString(this.settings.increments[this.incrementAY]);
            }
            else if (guiButton.id == this.btnDecAZ.id) {
                final Vector3f pointA5 = this.settings.pointA;
                pointA5.z -= this.settings.increments[this.incrementAZ];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnIncAZ.id) {
                final Vector3f pointA6 = this.settings.pointA;
                pointA6.z += this.settings.increments[this.incrementAZ];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnAmountAZ.id) {
                this.incrementAZ = (this.incrementAZ + 1) % this.settings.increments.length;
                this.btnAmountAZ.displayString = Integer.toString(this.settings.increments[this.incrementAZ]);
            }
            else if (guiButton.id == this.btnPointB.id) {
                this.settings.moveHere(this.settings.pointB);
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnDecBX.id) {
                final Vector3f pointB = this.settings.pointB;
                pointB.x -= this.settings.increments[this.incrementBX];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnIncBX.id) {
                final Vector3f pointB2 = this.settings.pointB;
                pointB2.x += this.settings.increments[this.incrementBX];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnAmountBX.id) {
                this.incrementBX = (this.incrementBX + 1) % this.settings.increments.length;
                this.btnAmountBX.displayString = Integer.toString(this.settings.increments[this.incrementBX]);
            }
            else if (guiButton.id == this.btnDecBY.id) {
                final Vector3f pointB3 = this.settings.pointB;
                pointB3.y -= this.settings.increments[this.incrementBY];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnIncBY.id) {
                final Vector3f pointB4 = this.settings.pointB;
                pointB4.y += this.settings.increments[this.incrementBY];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnAmountBY.id) {
                this.incrementBY = (this.incrementBY + 1) % this.settings.increments.length;
                this.btnAmountBY.displayString = Integer.toString(this.settings.increments[this.incrementBY]);
            }
            else if (guiButton.id == this.btnDecBZ.id) {
                final Vector3f pointB5 = this.settings.pointB;
                pointB5.z -= this.settings.increments[this.incrementBZ];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnIncBZ.id) {
                final Vector3f pointB6 = this.settings.pointB;
                pointB6.z += this.settings.increments[this.incrementBZ];
                this.settings.updatePoints();
            }
            else if (guiButton.id == this.btnAmountBZ.id) {
                this.incrementBZ = (this.incrementBZ + 1) % this.settings.increments.length;
                this.btnAmountBZ.displayString = Integer.toString(this.settings.increments[this.incrementBZ]);
            }
            else if (guiButton.id == this.btnEnable.id) {
                this.settings.isRenderingGuide = (!this.settings.isRenderingGuide && this.settings.isSaveEnabled);
                this.btnEnable.displayString = I18n.format(this.settings.isRenderingGuide ? "schematica.gui.disable" : "schematica.gui.enable", new Object[0]);
                this.btnSave.enabled = this.settings.isRenderingGuide;
            }
            else if (guiButton.id == this.btnSave.id) {
                final String path = this.tfFilename.getText() + ".schematic";
                if (this.settings.saveSchematic(Reference.schematicDirectory, path, this.settings.pointMin, this.settings.pointMax)) {
                    this.filename = "";
                    this.tfFilename.setText(this.filename);
                }
            }
        }
    }
    
    protected void mouseClicked(final int x, final int y, final int action) {
        this.tfFilename.mouseClicked(x, y, action);
        super.mouseClicked(x, y, action);
    }
    
    protected void keyTyped(final char character, final int code) {
        this.tfFilename.textboxKeyTyped(character, code);
        this.filename = this.tfFilename.getText();
        super.keyTyped(character, code);
    }
    
    public void updateScreen() {
        this.tfFilename.updateCursorCounter();
        super.updateScreen();
    }
    
    public void drawScreen(final int par1, final int par2, final float par3) {
        this.drawString(this.fontRendererObj, this.strSaveSelection, this.width - 205, this.height - 45, 16777215);
        this.drawString(this.fontRendererObj, this.strX, this.centerX - 145, this.centerY - 24, 16777215);
        this.drawString(this.fontRendererObj, Integer.toString((int)this.settings.pointA.x), this.centerX - 25, this.centerY - 24, 16777215);
        this.drawString(this.fontRendererObj, this.strY, this.centerX - 145, this.centerY + 1, 16777215);
        this.drawString(this.fontRendererObj, Integer.toString((int)this.settings.pointA.y), this.centerX - 25, this.centerY + 1, 16777215);
        this.drawString(this.fontRendererObj, this.strZ, this.centerX - 145, this.centerY + 26, 16777215);
        this.drawString(this.fontRendererObj, Integer.toString((int)this.settings.pointA.z), this.centerX - 25, this.centerY + 26, 16777215);
        this.drawString(this.fontRendererObj, this.strX, this.centerX + 15, this.centerY - 24, 16777215);
        this.drawString(this.fontRendererObj, Integer.toString((int)this.settings.pointB.x), this.centerX + 135, this.centerY - 24, 16777215);
        this.drawString(this.fontRendererObj, this.strY, this.centerX + 15, this.centerY + 1, 16777215);
        this.drawString(this.fontRendererObj, Integer.toString((int)this.settings.pointB.y), this.centerX + 135, this.centerY + 1, 16777215);
        this.drawString(this.fontRendererObj, this.strZ, this.centerX + 15, this.centerY + 26, 16777215);
        this.drawString(this.fontRendererObj, Integer.toString((int)this.settings.pointB.z), this.centerX + 135, this.centerY + 26, 16777215);
        this.tfFilename.drawTextBox();
        super.drawScreen(par1, par2, par3);
    }
}
