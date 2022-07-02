package net.minecraft.src;

import java.util.HashSet;

public class WrUpdateState
{
    public ChunkCache chunkcache = null;
    public RenderBlocks renderblocks = null;
    public HashSet setOldEntityRenders = null;
    public int renderPass = 0;
    public int y = 0;
    public boolean flag = false;
    public boolean hasRenderedBlocks = false;
    public boolean hasGlList = false;
}
