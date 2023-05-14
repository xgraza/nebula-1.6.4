//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.core.lib;

import com.github.lunatrius.schematica.config.Config;
import org.apache.logging.log4j.*;
import com.github.lunatrius.core.config.*;
import java.util.*;
import com.google.common.base.*;
import java.io.*;

public class Reference
{
    public static final String MODID = "LunatriusCore";
    public static final String NAME = "LunatriusCore";
    public static final String VERSION;
    public static final String FORGE;
    public static final String MINECRAFT;
    public static final String PROXY_COMMON = "com.github.lunatrius.core.CommonProxy";
    public static final String PROXY_CLIENT = "com.github.lunatrius.core.client.ClientProxy";
    public static Logger logger;
    public static Config config;
    
    static {
        final Properties prop = new Properties();
        try {
            final InputStream stream = Reference.class.getClassLoader().getResourceAsStream("version.properties");
            prop.load(stream);
            stream.close();
        }
        catch (Exception e) {
            Throwables.propagate((Throwable)e);
        }
        VERSION = prop.getProperty("version.mod");
        FORGE = prop.getProperty("version.forge");
        MINECRAFT = prop.getProperty("version.minecraft");
        Reference.logger = null;
        Reference.config = null;
    }
}
