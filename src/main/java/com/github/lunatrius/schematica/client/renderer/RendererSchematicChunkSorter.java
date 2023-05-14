//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.renderer;

import java.util.*;
import com.github.lunatrius.schematica.*;
import com.github.lunatrius.core.util.vector.*;

public class RendererSchematicChunkSorter implements Comparator
{
    private final Settings settings;
    
    public RendererSchematicChunkSorter() {
        this.settings = Settings.instance;
    }
    
    public int doCompare(final RendererSchematicChunk par1RendererSchematicChunk, final RendererSchematicChunk par2RendererSchematicChunk) {
        if (par1RendererSchematicChunk.isInFrustrum && !par2RendererSchematicChunk.isInFrustrum) {
            return -1;
        }
        if (!par1RendererSchematicChunk.isInFrustrum && par2RendererSchematicChunk.isInFrustrum) {
            return 1;
        }
        final Vector3f position = this.settings.playerPosition.clone().sub(this.settings.offset);
        final double dist1 = par1RendererSchematicChunk.distanceToPoint(position);
        final double dist2 = par2RendererSchematicChunk.distanceToPoint(position);
        return (dist1 > dist2) ? 1 : ((dist1 < dist2) ? -1 : 0);
    }
    
    @Override
    public int compare(final Object par1Obj, final Object par2Obj) {
        return this.doCompare((RendererSchematicChunk)par1Obj, (RendererSchematicChunk)par2Obj);
    }
}
