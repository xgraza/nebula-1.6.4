package shadersmod.uniform;

import net.optifine.entity.model.anim.ExpressionType;
import net.optifine.entity.model.anim.IExpression;
import net.optifine.entity.model.anim.IExpressionBool;
import net.optifine.entity.model.anim.IExpressionFloat;

public enum UniformType
{
    BOOL,
    INT,
    FLOAT;

    public ShaderUniformBase makeShaderUniform(String name)
    {
        switch (UniformType.NamelessClass1051388528.$SwitchMap$shadersmod$uniform$UniformType[this.ordinal()])
        {
            case 1:
                return new ShaderUniformInt(name);

            case 2:
                return new ShaderUniformInt(name);

            case 3:
                return new ShaderUniformFloat(name);

            default:
                throw new RuntimeException("Unknown uniform type: " + this);
        }
    }

    public void updateUniform(IExpression expression, ShaderUniformBase uniform)
    {
        switch (UniformType.NamelessClass1051388528.$SwitchMap$shadersmod$uniform$UniformType[this.ordinal()])
        {
            case 1:
                this.updateUniformBool((IExpressionBool)expression, (ShaderUniformInt)uniform);
                return;

            case 2:
                this.updateUniformInt((IExpressionFloat)expression, (ShaderUniformInt)uniform);
                return;

            case 3:
                this.updateUniformFloat((IExpressionFloat)expression, (ShaderUniformFloat)uniform);
                return;

            default:
                throw new RuntimeException("Unknown uniform type: " + this);
        }
    }

    private void updateUniformBool(IExpressionBool expression, ShaderUniformInt uniform)
    {
        boolean val = expression.eval();
        int valInt = val ? 1 : 0;
        uniform.setValue(valInt);
    }

    private void updateUniformInt(IExpressionFloat expression, ShaderUniformInt uniform)
    {
        int val = (int)expression.eval();
        uniform.setValue(val);
    }

    private void updateUniformFloat(IExpressionFloat expression, ShaderUniformFloat uniform)
    {
        float val = expression.eval();
        uniform.setValue(val);
    }

    public boolean matchesExpressionType(ExpressionType expressionType)
    {
        switch (UniformType.NamelessClass1051388528.$SwitchMap$shadersmod$uniform$UniformType[this.ordinal()])
        {
            case 1:
                return expressionType == ExpressionType.BOOL;

            case 2:
                return expressionType == ExpressionType.FLOAT;

            case 3:
                return expressionType == ExpressionType.FLOAT;

            default:
                throw new RuntimeException("Unknown uniform type: " + this);
        }
    }

    public static UniformType parse(String type)
    {
        UniformType[] values = values();

        for (int i = 0; i < values.length; ++i)
        {
            UniformType uniformType = values[i];

            if (uniformType.name().toLowerCase().equals(type))
            {
                return uniformType;
            }
        }

        return null;
    }

    static class NamelessClass1051388528 {
        static final int[] $SwitchMap$shadersmod$uniform$UniformType = new int[UniformType.values().length];

        static {
            try {
                $SwitchMap$shadersmod$uniform$UniformType[UniformType.BOOL.ordinal()] = 1;
            }
            catch (NoSuchFieldError var3)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$UniformType[UniformType.INT.ordinal()] = 2;
            }
            catch (NoSuchFieldError var2)
            {
                ;
            }

            try {
                $SwitchMap$shadersmod$uniform$UniformType[UniformType.FLOAT.ordinal()] = 3;
            }
            catch (NoSuchFieldError var1)
            {
                ;
            }
        }
    }
}
