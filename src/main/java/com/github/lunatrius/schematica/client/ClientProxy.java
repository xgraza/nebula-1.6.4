//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client;

import com.github.lunatrius.schematica.*;
import com.github.lunatrius.schematica.client.renderer.*;
import com.github.lunatrius.schematica.world.*;
import lol.nebula.Nebula;
import net.minecraft.client.settings.*;
import com.github.lunatrius.schematica.client.events.*;
import java.io.*;
import net.minecraft.client.*;
import net.minecraft.entity.player.*;
import org.apache.commons.lang3.ArrayUtils;

public class ClientProxy extends CommonProxy
{
    private RendererSchematicGlobal rendererSchematicGlobal;
    private SchematicWorld schematicWorld;
    
    public ClientProxy() {
        this.rendererSchematicGlobal = null;
        this.schematicWorld = null;
    }
    
    @Override
    public void registerKeybindings() {
        Minecraft.getMinecraft().gameSettings.keyBindings = ArrayUtils.addAll(KeyInputHandler.KEY_BINDINGS, Minecraft.getMinecraft().gameSettings.keyBindings);
        //for (KeyBinding keyBinding : Minecraft.getMinecraft().gameSettings.keyBindings) System.out.println(keyBinding);

//        for (final KeyBinding keyBinding : KeyInputHandler.KEY_BINDINGS) {
//            KeyBinding[] bindings = Minecraft.getMinecraft().gameSettings.keyBindings;
//            ArrayUtils.add(bindings, keyBinding);
//            //ClientRegistry.registerKeyBinding(keyBinding);
//        }
    }
    
    @Override
    public void registerEvents() {
        Nebula.getBus().subscribe(new KeyInputHandler());
        //FMLCommonHandler.instance().bus().register((Object)new KeyInputHandler());
        Nebula.getBus().subscribe(new TickHandler());
        //FMLCommonHandler.instance().bus().register((Object)new TickHandler());
        this.rendererSchematicGlobal = new RendererSchematicGlobal();
        //MinecraftForge.EVENT_BUS.register((Object)this.rendererSchematicGlobal);
        Nebula.getBus().subscribe(rendererSchematicGlobal);
        //MinecraftForge.EVENT_BUS.register((Object)new ChatEventHandler());
        Nebula.getBus().subscribe(new ChatEventHandler());
    }
    
    @Override
    public File getDataDirectory() {
        return Minecraft.getMinecraft().mcDataDir;
    }
    
    @Override
    public void setActiveSchematic(final SchematicWorld world) {
        this.schematicWorld = world;
    }
    
    @Override
    public void setActiveSchematic(final SchematicWorld world, final EntityPlayer player) {
        this.setActiveSchematic(world);
    }
    
    @Override
    public SchematicWorld getActiveSchematic() {
        return this.schematicWorld;
    }
    
    @Override
    public SchematicWorld getActiveSchematic(final EntityPlayer player) {
        return this.getActiveSchematic();
    }
}
