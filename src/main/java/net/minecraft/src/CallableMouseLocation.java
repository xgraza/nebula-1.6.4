package net.minecraft.src;

import java.util.concurrent.Callable;
import org.lwjgl.input.Mouse;

class CallableMouseLocation implements Callable
{
    final int field_90026_a;

    final int field_90024_b;

    final EntityRenderer theEntityRenderer;

    CallableMouseLocation(EntityRenderer par1EntityRenderer, int par2, int par3)
    {
        this.theEntityRenderer = par1EntityRenderer;
        this.field_90026_a = par2;
        this.field_90024_b = par3;
    }

    public String callMouseLocation()
    {
        return String.format("Scaled: (%d, %d). Absolute: (%d, %d)", new Object[] {Integer.valueOf(this.field_90026_a), Integer.valueOf(this.field_90024_b), Integer.valueOf(Mouse.getX()), Integer.valueOf(Mouse.getY())});
    }

    public Object call()
    {
        return this.callMouseLocation();
    }
}
