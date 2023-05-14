//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.core.util.vector;

public class Vector4i extends Vector3i
{
    public int w;
    
    public Vector4i() {
        this(0, 0, 0, 0);
    }
    
    public Vector4i(final Vector4i vec) {
        this(vec.x, vec.y, vec.z, vec.w);
    }
    
    public Vector4i(final int num) {
        this(num, num, num, num);
    }
    
    public Vector4i(final int x, final int y, final int z, final int w) {
        super(x, y, z);
        this.w = w;
    }
    
    public final int getW() {
        return this.w;
    }
    
    public final void setW(final int w) {
        this.w = w;
    }
    
    public Vector4i set(final Vector4i vec) {
        return this.set(vec.x, vec.y, vec.z, vec.w);
    }
    
    public Vector4i set(final int x, final int y, final int z, final int w) {
        super.set(x, y, z);
        this.w = w;
        return this;
    }
    
    public int lengthSquared() {
        return super.lengthSquared() + this.w * this.w;
    }
    
    public Vector4i negate() {
        super.negate();
        this.w = -this.w;
        return this;
    }
    
    public double dot(final Vector4i vec) {
        return super.dot((Vector3i)vec) + this.w * vec.w;
    }
    
    public Vector4i scale(final double scale) {
        super.scale(scale);
        this.w *= (int)scale;
        return this;
    }
    
    public Vector4i add(final Vector4i vec) {
        super.add((Vector3i)vec);
        this.w += vec.w;
        return this;
    }
    
    public Vector4i add(final int x, final int y, final int z, final int w) {
        super.add(x, y, z);
        this.w += w;
        return this;
    }
    
    public Vector4i sub(final Vector4i vec) {
        super.sub((Vector3i)vec);
        this.w -= vec.w;
        return this;
    }
    
    public Vector4i sub(final int x, final int y, final int z, final int w) {
        super.sub(x, y, z);
        this.w -= w;
        return this;
    }
    
    public Vector4f toVector4f() {
        return new Vector4f((float)this.x, (float)this.y, (float)this.z, (float)this.w);
    }
    
    public Vector4i clone() {
        return new Vector4i(this);
    }
    
    public boolean equals(final Object obj) {
        return obj instanceof Vector4i && this.equals((Vector4i)obj);
    }
    
    public boolean equals(final Vector4i vec) {
        return super.equals((Vector3i)vec) && this.w == vec.w;
    }
    
    public String toString() {
        return String.format("[%s, %s, %s, %s]", this.x, this.y, this.z, this.w);
    }
}
