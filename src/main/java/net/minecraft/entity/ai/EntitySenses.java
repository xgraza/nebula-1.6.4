package net.minecraft.entity.ai;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLiving;

public class EntitySenses
{
    EntityLiving entityObj;
    List seenEntities = new ArrayList();
    List unseenEntities = new ArrayList();
    private static final String __OBFID = "CL_00001628";

    public EntitySenses(EntityLiving par1EntityLiving)
    {
        this.entityObj = par1EntityLiving;
    }

    public void clearSensingCache()
    {
        this.seenEntities.clear();
        this.unseenEntities.clear();
    }

    public boolean canSee(Entity par1Entity)
    {
        if (this.seenEntities.contains(par1Entity))
        {
            return true;
        }
        else if (this.unseenEntities.contains(par1Entity))
        {
            return false;
        }
        else
        {
            this.entityObj.worldObj.theProfiler.startSection("canSee");
            boolean var2 = this.entityObj.canEntityBeSeen(par1Entity);
            this.entityObj.worldObj.theProfiler.endSection();

            if (var2)
            {
                this.seenEntities.add(par1Entity);
            }
            else
            {
                this.unseenEntities.add(par1Entity);
            }

            return var2;
        }
    }
}
