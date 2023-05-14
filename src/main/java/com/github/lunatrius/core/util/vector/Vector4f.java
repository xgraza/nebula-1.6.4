//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.core.util.vector;

public class Vector4f extends Vector3f
{
    public float w;
    
    public Vector4f() {
        this(0.0f, 0.0f, 0.0f, 0.0f);
    }
    
    public Vector4f(final Vector4f vec) {
        this(vec.x, vec.y, vec.z, vec.w);
    }
    
    public Vector4f(final float num) {
        this(num, num, num, num);
    }
    
    public Vector4f(final float x, final float y, final float z, final float w) {
        super(x, y, z);
        this.w = w;
    }
    
    public final float getW() {
        return this.w;
    }
    
    public final void setW(final float w) {
        this.w = w;
    }
    
    public Vector4f set(final Vector4f vec) {
        return this.set(vec.x, vec.y, vec.z, vec.w);
    }
    
    public Vector4f set(final float x, final float y, final float z, final float w) {
        super.set(x, y, z);
        this.w = w;
        return this;
    }
    
    public float lengthSquared() {
        return super.lengthSquared() + this.w * this.w;
    }
    
    public Vector4f negate() {
        super.negate();
        this.w = -this.w;
        return this;
    }
    
    public double dot(final Vector4i vec) {
        return super.dot((Vector3i)vec) + this.w * vec.w;
    }
    
    public Vector4f scale(final double scale) {
        super.scale(scale);
        this.w *= (float)scale;
        return this;
    }
    
    public Vector4f add(final Vector4f vec) {
        super.add((Vector3f)vec);
        this.w += vec.w;
        return this;
    }
    
    public Vector4f add(final float x, final float y, final float z, final float w) {
        super.add(x, y, z);
        this.w += w;
        return this;
    }
    
    public Vector4f sub(final Vector4f vec) {
        super.sub((Vector3f)vec);
        this.w -= vec.w;
        return this;
    }
    
    public Vector4f sub(final float x, final float y, final float z, final float w) {
        super.sub(x, y, z);
        this.w -= w;
        return this;
    }
    
    public Vector4i toVector4i() {
        return new Vector4i((int)Math.floor(this.x), (int)Math.floor(this.y), (int)Math.floor(this.z), (int)Math.floor(this.w));
    }
    
    public Vector4f clone() {
        return new Vector4f(this);
    }
    
    public boolean equals(final Object obj) {
        return obj instanceof Vector4f && this.equals((Vector4f)obj);
    }
    
    public boolean equals(final Vector4f vec) {
        return this.equals(vec, 1.0E-5f);
    }
    
    public boolean equals(final Vector4f vec, final float epsilon) {
        return super.equals((Vector3f)vec) && Math.abs(this.w - vec.w) < epsilon;
    }
    
    public String toString() {
        return String.format("[%s, %s, %s, %s]", this.x, this.y, this.z, this.w);
    }
}
