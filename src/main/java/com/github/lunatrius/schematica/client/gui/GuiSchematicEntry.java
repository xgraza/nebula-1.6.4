//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.gui;

import net.minecraft.item.*;
import net.minecraft.block.*;

public class GuiSchematicEntry
{
    private final String name;
    private final ItemStack itemStack;
    private final boolean isDirectory;
    
    public GuiSchematicEntry(final String name, final ItemStack itemStack, final boolean isDirectory) {
        this(name, itemStack.getItem(), itemStack.getItemDamage(), isDirectory);
    }
    
    public GuiSchematicEntry(final String name, final Item item, final int itemDamage, final boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.itemStack = new ItemStack(item, 1, itemDamage);
    }
    
    public GuiSchematicEntry(final String name, final Block block, final int itemDamage, final boolean isDirectory) {
        this.name = name;
        this.isDirectory = isDirectory;
        this.itemStack = new ItemStack(block, 1, itemDamage);
    }
    
    public String getName() {
        return this.name;
    }
    
    public Item getItem() {
        return this.itemStack.getItem();
    }
    
    public int getItemDamage() {
        return this.itemStack.getItemDamage();
    }
    
    public boolean isDirectory() {
        return this.isDirectory;
    }
    
    public ItemStack getItemStack() {
        return this.itemStack;
    }
}
