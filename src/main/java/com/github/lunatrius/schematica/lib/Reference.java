//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.lib;

import org.apache.logging.log4j.*;
import com.github.lunatrius.schematica.config.*;
import java.util.*;
import com.google.common.base.*;
import java.io.*;

public class Reference
{
    public static final String MODID = "Schematica";
    public static final String NAME = "Schematica";
    public static final String VERSION = "penis";
    public static final String FORGE = "forge_fucking_sucks_dick";
    public static final String MINECRAFT = "1.7.2";
    public static final String PROXY_COMMON = "com.github.lunatrius.schematica.CommonProxy";
    public static final String PROXY_CLIENT = "com.github.lunatrius.schematica.client.ClientProxy";
    public static Logger logger;
    public static Config config;
    public static File schematicDirectory;
    
    static {
        Reference.logger = null;
        Reference.config = null;
        Reference.schematicDirectory = new File(".", "schematics");
    }
}
