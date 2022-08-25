package shadersmod.uniform;

import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.EntityLivingBase;
import net.optifine.entity.model.anim.ExpressionType;
import net.optifine.entity.model.anim.IExpressionBool;

public enum ShaderParameterBool implements IExpressionBool
{
    IS_ALIVE("is_alive"),
    IS_BURNING("is_burning"),
    IS_CHILD("is_child"),
    IS_GLOWING("is_glowing"),
    IS_HURT("is_hurt"),
    IS_IN_LAVA("is_in_lava"),
    IS_IN_WATER("is_in_water"),
    IS_INVISIBLE("is_invisible"),
    IS_ON_GROUND("is_on_ground"),
    IS_RIDDEN("is_ridden"),
    IS_RIDING("is_riding"),
    IS_SNEAKING("is_sneaking"),
    IS_SPRINTING("is_sprinting"),
    IS_WET("is_wet");
    private String name;
    private RenderManager renderManager;
    private static final ShaderParameterBool[] VALUES = values();

    private ShaderParameterBool(String name)
    {
        this.name = name;
        this.renderManager = RenderManager.instance;
    }

    public String getName()
    {
        return this.name;
    }

    public ExpressionType getExpressionType()
    {
        return ExpressionType.BOOL;
    }

    public boolean eval()
    {
        EntityLivingBase entityGeneral = Minecraft.getMinecraft().renderViewEntity;

        if (entityGeneral instanceof EntityLivingBase)
        {
            EntityLivingBase entity = (EntityLivingBase)entityGeneral;

            switch (ShaderParameterBool.NamelessClass1456163465.$SwitchMap$shadersmod$uniform$ShaderParameterBool[this.ordinal()])
            {
                case 1:
                    return entity.isEntityAlive();

                case 2:
                    return entity.isBurning();

                case 3:
                    return entity.isChild();

                case 4:
                    return false;

                case 5:
                    return entity.hurtTime > 0;

                case 6:
                    return entity.handleLavaMovement();

                case 7:
                    return entity.isInWater();

                case 8:
                    return entity.isInvisible();

                case 9:
                    return entity.onGround;

                case 10:
                    return entity.riddenByEntity != null;

                case 11:
                    return entity.isRiding();

                case 12:
                    return entity.isSneaking();

                case 13:
                    return entity.isSprinting();

                case 14:
                    return entity.isWet();
            }
        }

        return false;
    }

    public static ShaderParameterBool parse(String str)
    {
        if (str == null)
        {
            return null;
        }
        else
        {
            for (int i = 0; i < VALUES.length; ++i)
            {
                ShaderParameterBool type = VALUES[i];

                if (type.getName().equals(str))
                {
                    return type;
                }
            }

            return null;
        }
    }

    static class NamelessClass1456163465 {
        static final int[] $SwitchMap$shadersmod$uniform$ShaderParameterBool = new int[ShaderParameterBool.values().length];

        static {
            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_ALIVE.ordinal()] = 1;
            }
            catch (NoSuchFieldError var14)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_BURNING.ordinal()] = 2;
            }
            catch (NoSuchFieldError var13)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_CHILD.ordinal()] = 3;
            }
            catch (NoSuchFieldError var12)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_GLOWING.ordinal()] = 4;
            }
            catch (NoSuchFieldError var11)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_HURT.ordinal()] = 5;
            }
            catch (NoSuchFieldError var10)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_IN_LAVA.ordinal()] = 6;
            }
            catch (NoSuchFieldError var9)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_IN_WATER.ordinal()] = 7;
            }
            catch (NoSuchFieldError var8)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_INVISIBLE.ordinal()] = 8;
            }
            catch (NoSuchFieldError var7)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_ON_GROUND.ordinal()] = 9;
            }
            catch (NoSuchFieldError var6)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_RIDDEN.ordinal()] = 10;
            }
            catch (NoSuchFieldError var5)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_RIDING.ordinal()] = 11;
            }
            catch (NoSuchFieldError var4)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_SNEAKING.ordinal()] = 12;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_SPRINTING.ordinal()] = 13;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$ShaderParameterBool[ShaderParameterBool.IS_WET.ordinal()] = 14;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
