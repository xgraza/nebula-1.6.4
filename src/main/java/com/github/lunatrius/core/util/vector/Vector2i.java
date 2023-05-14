//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.core.util.vector;

public class Vector2i
{
    public int x;
    public int y;
    
    public Vector2i() {
        this(0, 0);
    }
    
    public Vector2i(final Vector2i vec) {
        this(vec.x, vec.y);
    }
    
    public Vector2i(final int num) {
        this(num, num);
    }
    
    public Vector2i(final int x, final int y) {
        this.x = x;
        this.y = y;
    }
    
    public final int getX() {
        return this.x;
    }
    
    public final int getY() {
        return this.y;
    }
    
    public final void setX(final int x) {
        this.x = x;
    }
    
    public final void setY(final int y) {
        this.y = y;
    }
    
    public Vector2i set(final Vector2i vec) {
        return this.set(vec.x, vec.y);
    }
    
    public Vector2i set(final int x, final int y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public final double length() {
        return Math.sqrt(this.lengthSquared());
    }
    
    public int lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }
    
    public final Vector2i normalize() {
        final double len = this.length();
        if (len != 0.0) {
            return this.scale(1.0 / len);
        }
        return this;
    }
    
    public Vector2i negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }
    
    public double dot(final Vector2i vec) {
        return this.x * vec.x + this.y * vec.y;
    }
    
    public Vector2i scale(final double scale) {
        this.x *= (int)scale;
        this.y *= (int)scale;
        return this;
    }
    
    public Vector2i add(final Vector2i vec) {
        this.x += vec.x;
        this.y += vec.y;
        return this;
    }
    
    public Vector2i add(final int x, final int y) {
        this.x += x;
        this.y += y;
        return this;
    }
    
    public Vector2i sub(final Vector2i vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        return this;
    }
    
    public Vector2i sub(final int x, final int y) {
        this.x -= x;
        this.y -= y;
        return this;
    }
    
    public Vector2f toVector2f() {
        return new Vector2f((float)this.x, (float)this.y);
    }
    
    public Vector2i clone() {
        return new Vector2i(this);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Vector2i && this.equals((Vector2i)obj);
    }
    
    public boolean equals(final Vector2i vec) {
        return this.x == vec.x && this.y == vec.y;
    }
    
    @Override
    public String toString() {
        return String.format("[%s, %s]", this.x, this.y);
    }
}
