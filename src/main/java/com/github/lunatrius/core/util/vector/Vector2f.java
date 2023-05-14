//Deobfuscated with https://github.com/SimplyProgrammer/Minecraft-Deobfuscator3000 using mappings "C:\Users\Gavin\Documents\schematica\mappings"!

//Decompiled by Procyon!

package com.github.lunatrius.core.util.vector;

public class Vector2f
{
    public static final float FLOAT_EPSILON = 1.0E-5f;
    public float x;
    public float y;
    
    public Vector2f() {
        this(0.0f, 0.0f);
    }
    
    public Vector2f(final Vector2f vec) {
        this(vec.x, vec.y);
    }
    
    public Vector2f(final float num) {
        this(num, num);
    }
    
    public Vector2f(final float x, final float y) {
        this.x = x;
        this.y = y;
    }
    
    public final float getX() {
        return this.x;
    }
    
    public final float getY() {
        return this.y;
    }
    
    public final void setX(final float x) {
        this.x = x;
    }
    
    public final void setY(final float y) {
        this.y = y;
    }
    
    public Vector2f set(final Vector2f vec) {
        return this.set(vec.x, vec.y);
    }
    
    public Vector2f set(final float x, final float y) {
        this.x = x;
        this.y = y;
        return this;
    }
    
    public final double length() {
        return Math.sqrt(this.lengthSquared());
    }
    
    public float lengthSquared() {
        return this.x * this.x + this.y * this.y;
    }
    
    public final Vector2f normalize() {
        final double len = this.length();
        if (len != 0.0) {
            return this.scale(1.0 / len);
        }
        return this;
    }
    
    public Vector2f negate() {
        this.x = -this.x;
        this.y = -this.y;
        return this;
    }
    
    public double dot(final Vector2i vec) {
        return this.x * vec.x + this.y * vec.y;
    }
    
    public Vector2f scale(final double scale) {
        this.x *= (float)scale;
        this.y *= (float)scale;
        return this;
    }
    
    public Vector2f add(final Vector2f vec) {
        this.x += vec.x;
        this.y += vec.y;
        return this;
    }
    
    public Vector2f add(final float x, final float y) {
        this.x += x;
        this.y += y;
        return this;
    }
    
    public Vector2f sub(final Vector2f vec) {
        this.x -= vec.x;
        this.y -= vec.y;
        return this;
    }
    
    public Vector2f sub(final float x, final float y) {
        this.x -= x;
        this.y -= y;
        return this;
    }
    
    public Vector2i toVector2i() {
        return new Vector2i((int)Math.floor(this.x), (int)Math.floor(this.y));
    }
    
    public Vector2f clone() {
        return new Vector2f(this);
    }
    
    @Override
    public boolean equals(final Object obj) {
        return obj instanceof Vector2f && this.equals((Vector2f)obj);
    }
    
    public boolean equals(final Vector2f vec) {
        return this.equals(vec, 1.0E-5f);
    }
    
    public boolean equals(final Vector2f vec, final float epsilon) {
        return Math.abs(this.x - vec.x) < epsilon && Math.abs(this.y - vec.y) < epsilon;
    }
    
    @Override
    public String toString() {
        return String.format("[%s, %s]", this.x, this.y);
    }
}
