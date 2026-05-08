package com.github.lunatrius.schematica;

import com.github.lunatrius.schematica.client.events.KeyInputHandler;
import com.github.lunatrius.schematica.client.events.TickHandler;
import com.github.lunatrius.schematica.client.renderer.RendererSchematicGlobal;
import com.github.lunatrius.schematica.config.Config;
import com.github.lunatrius.schematica.lib.Reference;
import com.github.lunatrius.schematica.world.SchematicWorld;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import us.nebula.client.listener.EventBus;

import java.io.File;

public class Schematica
{
    public static Schematica INSTANCE;

    private RendererSchematicGlobal rendererSchematicGlobal = null;
    private SchematicWorld schematicWorld = null;

    private Schematica()
    {
        createFolders();
        registerEvents();
    }

    public void createFolders()
    {
        if (!Reference.schematicDirectory.exists())
        {
            if (!Reference.schematicDirectory.mkdirs())
            {
                Reference.logger.info("Could not create schematic directory [%s]!", Reference.schematicDirectory.getAbsolutePath());
            }
        }

        Reference.config = new Config(new File(Reference.schematicDirectory, "config.json"));
    }

    public void registerEvents()
    {
        //FMLCommonHandler.instance().bus().register(new KeyInputHandler());
        //FMLCommonHandler.instance().bus().register(new TickHandler());
        EventBus.subscribe(new KeyInputHandler());
        EventBus.subscribe(new TickHandler());

        this.rendererSchematicGlobal = new RendererSchematicGlobal();
        EventBus.subscribe(rendererSchematicGlobal);
        //MinecraftForge.EVENT_BUS.register(this.rendererSchematicGlobal);
    }

    public void setActiveSchematic(SchematicWorld world)
    {
        this.schematicWorld = world;
    }

    public void setActiveSchematic(SchematicWorld world, EntityPlayer player)
    {
        setActiveSchematic(world);
    }

    public SchematicWorld getActiveSchematic()
    {
        return this.schematicWorld;
    }

    public SchematicWorld getActiveSchematic(EntityPlayer player)
    {
        return getActiveSchematic();
    }

    public static void load()
    {
        Reference.logger.info("Loading MCP Schematica");
        INSTANCE = new Schematica();
    }

    public static File getDataDirectory()
    {
        return Minecraft.getMinecraft().mcDataDir;
    }
}
