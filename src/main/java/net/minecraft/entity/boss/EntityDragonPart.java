package net.minecraft.entity.boss;

import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityMultiPart;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.DamageSource;

public class EntityDragonPart extends Entity
{
    public final IEntityMultiPart entityDragonObj;
    public final String field_146032_b;
    private static final String __OBFID = "CL_00001657";

    public EntityDragonPart(IEntityMultiPart par1IEntityMultiPart, String par2Str, float par3, float par4)
    {
        super(par1IEntityMultiPart.func_82194_d());
        this.setSize(par3, par4);
        this.entityDragonObj = par1IEntityMultiPart;
        this.field_146032_b = par2Str;
    }

    protected void entityInit() {}

    protected void readEntityFromNBT(NBTTagCompound par1NBTTagCompound) {}

    protected void writeEntityToNBT(NBTTagCompound par1NBTTagCompound) {}

    public boolean canBeCollidedWith()
    {
        return true;
    }

    public boolean attackEntityFrom(DamageSource par1DamageSource, float par2)
    {
        return this.isEntityInvulnerable() ? false : this.entityDragonObj.attackEntityFromPart(this, par1DamageSource, par2);
    }

    public boolean isEntityEqual(Entity par1Entity)
    {
        return this == par1Entity || this.entityDragonObj == par1Entity;
    }
}
