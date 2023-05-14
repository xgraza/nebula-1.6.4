//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.config;

import com.github.lunatrius.core.config.*;
import lol.nebula.setting.Setting;
import com.github.lunatrius.schematica.*;
import com.github.lunatrius.core.lib.*;
import java.io.*;

public class Config extends Configuration
{
    public final Setting<Boolean> propEnableAlpha;
    public final Setting<Double> propAlpha;
    public final Setting<Boolean> propHighlight;
    public final Setting<Boolean> propHighlightAir;
    public final Setting<Double> propBlockDelta;
    public final Setting<Integer> propPlaceDelay;
    public final Setting<Integer> propTimeout;
    public final Setting<Boolean> propPlaceInstantly;
    public final Setting<Boolean> propPlaceAdjacent;
    public final Setting<Boolean> propDrawQuads;
    public final Setting<Boolean> propDrawLines;
    public final Setting<String> propSchematicDirectory;
    public boolean enableAlpha;
    public float alpha;
    public boolean highlight;
    public boolean highlightAir;
    public float blockDelta;
    public int placeDelay;
    public int timeout;
    public boolean placeInstantly;
    public boolean placeAdjacent;
    public boolean drawQuads;
    public boolean drawLines;
    public File schematicDirectory;
    
    public Config(final File file) {
        super(file);
        this.enableAlpha = false;
        this.alpha = 1.0f;
        this.highlight = true;
        this.highlightAir = true;
        this.blockDelta = 0.005f;
        this.placeDelay = 1;
        this.timeout = 10;
        this.placeInstantly = false;
        this.placeAdjacent = true;
        this.drawQuads = true;
        this.drawLines = true;
        this.schematicDirectory = new File(Schematica.proxy.getDataDirectory(), "schematics");
        String directory;
        try {
            directory = this.schematicDirectory.getCanonicalPath();
        }
        catch (IOException e) {
            Reference.logger.info("Failed to get path!");
            directory = this.schematicDirectory.getAbsolutePath();
        }

        this.propEnableAlpha = new Setting<>(enableAlpha, "alphaEnabled");
        this.propAlpha = new Setting<>(0.0, 0.01, 0.0, 1.0, "alpha");
        this.propHighlight = new Setting<>(true, "propHighlight");
        this.propHighlightAir = new Setting<>(true, "propHighlightAir");
        this.propBlockDelta = new Setting<>(0.005, 0.1, 0.0, 0.5, "blockDelta");
        this.propPlaceDelay = new Setting<>(1, 0, 20, "propPlaceDelay");
        this.propTimeout = new Setting<>(10, 0, 100, "propTimeout");
        this.propPlaceInstantly = new Setting<>(false, "propPlaceInstantly");
        this.propPlaceAdjacent = new Setting<>(true, "propPlaceAdjacent");
        this.propDrawQuads = new Setting<>(true, "propDrawQuads");
        this.propDrawLines = new Setting<>(true, "propDrawLines");
        this.propSchematicDirectory = new Setting<>(directory, "schematicDirectory");

        this.enableAlpha = propEnableAlpha.getValue();
        this.alpha = propAlpha.getValue().floatValue();
        this.highlight = propHighlight.getValue();
        this.highlightAir = propHighlightAir.getValue();
        this.blockDelta = propBlockDelta.getValue().floatValue();
        this.placeDelay = propPlaceDelay.getValue();
        this.timeout = propTimeout.getValue();
        this.placeInstantly = propPlaceInstantly.getValue();
        this.placeAdjacent = propPlaceAdjacent.getValue();
        this.drawQuads = propDrawQuads.getValue();
        this.drawLines = propDrawLines.getValue();
        this.schematicDirectory = new File(propSchematicDirectory.getValue());
    }
}
