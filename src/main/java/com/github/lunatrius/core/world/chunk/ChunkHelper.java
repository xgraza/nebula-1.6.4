//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.core.world.chunk;

import java.util.*;

public class ChunkHelper
{
    public static boolean isSlimeChunk(final long seed, final int x, final int z) {
        return new Random(seed + x * x * 4987142 + x * 5947611 + z * z * 4392871 + z * 389711 ^ 0x3AD8025FL).nextInt(10) == 0;
    }
}
