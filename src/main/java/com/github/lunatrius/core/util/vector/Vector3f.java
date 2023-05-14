//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.core.util.vector;

public class Vector3f extends Vector2f
{
    public float z;
    
    public Vector3f() {
        this(0.0f, 0.0f, 0.0f);
    }
    
    public Vector3f(final Vector3f vec) {
        this(vec.x, vec.y, vec.z);
    }
    
    public Vector3f(final float num) {
        this(num, num, num);
    }
    
    public Vector3f(final float x, final float y, final float z) {
        super(x, y);
        this.z = z;
    }
    
    public final float getZ() {
        return this.z;
    }
    
    public final void setZ(final float z) {
        this.z = z;
    }
    
    public Vector3f set(final Vector3f vec) {
        return this.set(vec.x, vec.y, vec.z);
    }
    
    public Vector3f set(final float x, final float y, final float z) {
        super.set(x, y);
        this.z = z;
        return this;
    }
    
    public float lengthSquared() {
        return super.lengthSquared() + this.z * this.z;
    }
    
    public Vector3f negate() {
        super.negate();
        this.z = -this.z;
        return this;
    }
    
    public double dot(final Vector3i vec) {
        return super.dot((Vector2i)vec) + this.z * vec.z;
    }
    
    public Vector3f scale(final double scale) {
        super.scale(scale);
        this.z *= (float)scale;
        return this;
    }
    
    public Vector3f add(final Vector3f vec) {
        super.add((Vector2f)vec);
        this.z += vec.z;
        return this;
    }
    
    public Vector3f add(final float x, final float y, final float z) {
        super.add(x, y);
        this.z += z;
        return this;
    }
    
    public Vector3f sub(final Vector3f vec) {
        super.sub((Vector2f)vec);
        this.z -= vec.z;
        return this;
    }
    
    public Vector3f sub(final float x, final float y, final float z) {
        super.sub(x, y);
        this.z -= z;
        return this;
    }
    
    public Vector3i toVector3i() {
        return new Vector3i((int)Math.floor(this.x), (int)Math.floor(this.y), (int)Math.floor(this.z));
    }
    
    public Vector3f clone() {
        return new Vector3f(this);
    }
    
    public boolean equals(final Object obj) {
        return obj instanceof Vector3f && this.equals((Vector3f)obj);
    }
    
    public boolean equals(final Vector3f vec) {
        return this.equals(vec, 1.0E-5f);
    }
    
    public boolean equals(final Vector3f vec, final float epsilon) {
        return super.equals((Vector2f)vec) && Math.abs(this.z - vec.z) < epsilon;
    }
    
    public String toString() {
        return String.format("[%s, %s, %s]", this.x, this.y, this.z);
    }
}
