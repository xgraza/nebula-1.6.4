//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.core.util.vector;

public class Vector3i extends Vector2i
{
    public int z;
    
    public Vector3i() {
        this(0, 0, 0);
    }
    
    public Vector3i(final Vector3i vec) {
        this(vec.x, vec.y, vec.z);
    }
    
    public Vector3i(final int num) {
        this(num, num, num);
    }
    
    public Vector3i(final int x, final int y, final int z) {
        super(x, y);
        this.z = z;
    }
    
    public final int getZ() {
        return this.z;
    }
    
    public final void setZ(final int z) {
        this.z = z;
    }
    
    public Vector3i set(final Vector3i vec) {
        return this.set(vec.x, vec.y, vec.z);
    }
    
    public Vector3i set(final int x, final int y, final int z) {
        super.set(x, y);
        this.z = z;
        return this;
    }
    
    public int lengthSquared() {
        return super.lengthSquared() + this.z * this.z;
    }
    
    public Vector3i negate() {
        super.negate();
        this.z = -this.z;
        return this;
    }
    
    public double dot(final Vector3i vec) {
        return super.dot((Vector2i)vec) + this.z * vec.z;
    }
    
    public Vector3i scale(final double scale) {
        super.scale(scale);
        this.z *= (int)scale;
        return this;
    }
    
    public Vector3i add(final Vector3i vec) {
        super.add((Vector2i)vec);
        this.z += vec.z;
        return this;
    }
    
    public Vector3i add(final int x, final int y, final int z) {
        super.add(x, y);
        this.z += z;
        return this;
    }
    
    public Vector3i sub(final Vector3i vec) {
        super.sub((Vector2i)vec);
        this.z -= vec.z;
        return this;
    }
    
    public Vector3i sub(final int x, final int y, final int z) {
        super.sub(x, y);
        this.z -= z;
        return this;
    }
    
    public Vector3f toVector3f() {
        return new Vector3f((float)this.x, (float)this.y, (float)this.z);
    }
    
    public Vector3i clone() {
        return new Vector3i(this);
    }
    
    public boolean equals(final Object obj) {
        return obj instanceof Vector3i && this.equals((Vector3i)obj);
    }
    
    public boolean equals(final Vector3i vec) {
        return super.equals((Vector2i)vec) && this.z == vec.z;
    }
    
    public String toString() {
        return String.format("[%s, %s, %s]", this.x, this.y, this.z);
    }
}
