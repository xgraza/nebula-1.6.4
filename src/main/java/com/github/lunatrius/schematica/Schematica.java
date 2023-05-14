//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica;

import com.github.lunatrius.schematica.client.ClientProxy;
import com.github.lunatrius.schematica.config.Config;
import com.github.lunatrius.schematica.lib.Reference;
import org.apache.logging.log4j.LogManager;

import java.io.File;

public class Schematica
{
    public static CommonProxy proxy = new ClientProxy();

    public static void init() {
        Reference.logger = LogManager.getLogger("Schematica");
        Reference.config = new Config(new File(proxy.getDataDirectory(), "schematica_config.cfg"));
        Reference.config.save();

        // proxy shit
        Schematica.proxy.registerKeybindings();
        Schematica.proxy.createFolders();
        Schematica.proxy.registerEvents();
    }
}
