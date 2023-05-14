//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.schematica.client.renderer;

import com.github.lunatrius.core.util.vector.*;
import java.nio.*;
import org.lwjgl.*;
import com.github.lunatrius.schematica.lib.*;

public class RenderHelper
{
    public static final int QUAD_DOWN = 1;
    public static final int QUAD_UP = 2;
    public static final int QUAD_NORTH = 4;
    public static final int QUAD_SOUTH = 8;
    public static final int QUAD_WEST = 16;
    public static final int QUAD_EAST = 32;
    public static final int QUAD_ALL = 63;
    public static final int LINE_DOWN_WEST = 17;
    public static final int LINE_UP_WEST = 18;
    public static final int LINE_DOWN_EAST = 33;
    public static final int LINE_UP_EAST = 34;
    public static final int LINE_DOWN_NORTH = 5;
    public static final int LINE_UP_NORTH = 6;
    public static final int LINE_DOWN_SOUTH = 9;
    public static final int LINE_UP_SOUTH = 10;
    public static final int LINE_NORTH_WEST = 20;
    public static final int LINE_NORTH_EAST = 36;
    public static final int LINE_SOUTH_WEST = 24;
    public static final int LINE_SOUTH_EAST = 40;
    public static final int LINE_ALL = 63;
    public static final Vector3f VEC_ZERO;
    private static int quadSize;
    private static float[] quadVertexBuffer;
    private static float[] quadColorBuffer;
    private static int quadVertexIndex;
    private static int quadColorIndex;
    private static int quadCount;
    private static int lineSize;
    private static float[] lineVertexBuffer;
    private static float[] lineColorBuffer;
    private static int lineVertexIndex;
    private static int lineColorIndex;
    private static int lineCount;
    private static final Vector3f vecZero;
    private static final Vector3f vecSize;
    
    public static void createBuffers() {
        RenderHelper.quadSize = 240;
        RenderHelper.quadVertexBuffer = new float[RenderHelper.quadSize * 3];
        RenderHelper.quadColorBuffer = new float[RenderHelper.quadSize * 4];
        RenderHelper.lineSize = 240;
        RenderHelper.lineVertexBuffer = new float[RenderHelper.lineSize * 3];
        RenderHelper.lineColorBuffer = new float[RenderHelper.lineSize * 4];
        initBuffers();
    }
    
    public static void initBuffers() {
        RenderHelper.quadVertexIndex = 0;
        RenderHelper.quadColorIndex = 0;
        RenderHelper.quadCount = 0;
        RenderHelper.lineVertexIndex = 0;
        RenderHelper.lineColorIndex = 0;
        RenderHelper.lineCount = 0;
    }
    
    public static void destroyBuffers() {
        RenderHelper.quadSize = 0;
        RenderHelper.quadVertexBuffer = null;
        RenderHelper.quadColorBuffer = null;
        RenderHelper.lineSize = 0;
        RenderHelper.lineVertexBuffer = null;
        RenderHelper.lineColorBuffer = null;
    }
    
    public static FloatBuffer getQuadVertexBuffer() {
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(RenderHelper.quadVertexBuffer.length).put(RenderHelper.quadVertexBuffer);
        buffer.flip();
        return buffer;
    }
    
    public static FloatBuffer getQuadColorBuffer() {
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(RenderHelper.quadColorBuffer.length).put(RenderHelper.quadColorBuffer);
        buffer.flip();
        return buffer;
    }
    
    public static int getQuadCount() {
        return RenderHelper.quadCount;
    }
    
    public static FloatBuffer getLineVertexBuffer() {
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(RenderHelper.lineVertexBuffer.length).put(RenderHelper.lineVertexBuffer);
        buffer.flip();
        return buffer;
    }
    
    public static FloatBuffer getLineColorBuffer() {
        final FloatBuffer buffer = BufferUtils.createFloatBuffer(RenderHelper.lineColorBuffer.length).put(RenderHelper.lineColorBuffer);
        buffer.flip();
        return buffer;
    }
    
    public static int getLineCount() {
        return RenderHelper.lineCount;
    }
    
    private static float[] createAndCopyBuffer(final int newSize, final float[] oldBuffer) {
        final float[] tempBuffer = new float[newSize];
        System.arraycopy(oldBuffer, 0, tempBuffer, 0, oldBuffer.length);
        return tempBuffer;
    }
    
    public static void drawCuboidSurface(final Vector3f zero, final Vector3f size, final int sides, final float red, final float green, final float blue, final float alpha) {
        RenderHelper.vecZero.set(zero.x - Reference.config.blockDelta, zero.y - Reference.config.blockDelta, zero.z - Reference.config.blockDelta);
        RenderHelper.vecSize.set(size.x + Reference.config.blockDelta, size.y + Reference.config.blockDelta, size.z + Reference.config.blockDelta);
        if (RenderHelper.quadCount + 24 >= RenderHelper.quadSize) {
            RenderHelper.quadSize *= 2;
            RenderHelper.quadVertexBuffer = createAndCopyBuffer(RenderHelper.quadSize * 3, RenderHelper.quadVertexBuffer);
            RenderHelper.quadColorBuffer = createAndCopyBuffer(RenderHelper.quadSize * 4, RenderHelper.quadColorBuffer);
        }
        int total = 0;
        if ((sides & 0x1) != 0x0) {
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            total += 4;
        }
        if ((sides & 0x2) != 0x0) {
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            total += 4;
        }
        if ((sides & 0x4) != 0x0) {
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            total += 4;
        }
        if ((sides & 0x8) != 0x0) {
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            total += 4;
        }
        if ((sides & 0x10) != 0x0) {
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            total += 4;
        }
        if ((sides & 0x20) != 0x0) {
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.quadCount;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.quadVertexBuffer[RenderHelper.quadVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.quadCount;
            total += 4;
        }
        for (int i = 0; i < total; ++i) {
            RenderHelper.quadColorBuffer[RenderHelper.quadColorIndex++] = red;
            RenderHelper.quadColorBuffer[RenderHelper.quadColorIndex++] = green;
            RenderHelper.quadColorBuffer[RenderHelper.quadColorIndex++] = blue;
            RenderHelper.quadColorBuffer[RenderHelper.quadColorIndex++] = alpha;
        }
    }
    
    public static void drawCuboidOutline(final Vector3f zero, final Vector3f size, final int sides, final float red, final float green, final float blue, final float alpha) {
        RenderHelper.vecZero.set(zero.x - Reference.config.blockDelta, zero.y - Reference.config.blockDelta, zero.z - Reference.config.blockDelta);
        RenderHelper.vecSize.set(size.x + Reference.config.blockDelta, size.y + Reference.config.blockDelta, size.z + Reference.config.blockDelta);
        if (RenderHelper.lineCount + 24 >= RenderHelper.lineSize) {
            RenderHelper.lineSize *= 2;
            RenderHelper.lineVertexBuffer = createAndCopyBuffer(RenderHelper.lineSize * 3, RenderHelper.lineVertexBuffer);
            RenderHelper.lineColorBuffer = createAndCopyBuffer(RenderHelper.lineSize * 4, RenderHelper.lineColorBuffer);
        }
        int total = 0;
        if ((sides & 0x11) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        if ((sides & 0x12) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        if ((sides & 0x21) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        if ((sides & 0x22) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        if ((sides & 0x5) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        if ((sides & 0x6) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        if ((sides & 0x9) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        if ((sides & 0xA) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        if ((sides & 0x14) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        if ((sides & 0x24) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        if ((sides & 0x18) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        if ((sides & 0x28) != 0x0) {
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecZero.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.x;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.y;
            RenderHelper.lineVertexBuffer[RenderHelper.lineVertexIndex++] = RenderHelper.vecSize.z;
            ++RenderHelper.lineCount;
            total += 2;
        }
        for (int i = 0; i < total; ++i) {
            RenderHelper.lineColorBuffer[RenderHelper.lineColorIndex++] = red;
            RenderHelper.lineColorBuffer[RenderHelper.lineColorIndex++] = green;
            RenderHelper.lineColorBuffer[RenderHelper.lineColorIndex++] = blue;
            RenderHelper.lineColorBuffer[RenderHelper.lineColorIndex++] = alpha;
        }
    }
    
    static {
        VEC_ZERO = new Vector3f(0.0f, 0.0f, 0.0f);
        RenderHelper.quadSize = 0;
        RenderHelper.quadVertexBuffer = null;
        RenderHelper.quadColorBuffer = null;
        RenderHelper.quadVertexIndex = 0;
        RenderHelper.quadColorIndex = 0;
        RenderHelper.quadCount = 0;
        RenderHelper.lineSize = 0;
        RenderHelper.lineVertexBuffer = null;
        RenderHelper.lineColorBuffer = null;
        RenderHelper.lineVertexIndex = 0;
        RenderHelper.lineColorIndex = 0;
        RenderHelper.lineCount = 0;
        vecZero = new Vector3f();
        vecSize = new Vector3f();
    }
}
