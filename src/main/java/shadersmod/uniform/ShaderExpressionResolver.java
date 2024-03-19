package shadersmod.uniform;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;
import net.minecraft.world.biome.BiomeGenBase;
import net.optifine.entity.model.anim.ConstantFloat;
import net.optifine.entity.model.anim.IExpression;
import net.optifine.entity.model.anim.IExpressionResolver;
import shadersmod.common.SMCLog;

public class ShaderExpressionResolver implements IExpressionResolver
{
    private Map<String, IExpression> mapExpressions = new HashMap();

    public ShaderExpressionResolver(Map<String, IExpression> map)
    {
        this.registerExpressions();
        Set keys = map.keySet();
        Iterator it = keys.iterator();

        while (it.hasNext())
        {
            String name = (String)it.next();
            IExpression expr = (IExpression)map.get(name);
            this.registerExpression(name, expr);
        }
    }

    private void registerExpressions()
    {
        ShaderParameterFloat[] spfs = ShaderParameterFloat.values();

        for (int spbs = 0; spbs < spfs.length; ++spbs)
        {
            ShaderParameterFloat biomeList = spfs[spbs];
            this.mapExpressions.put(biomeList.getName(), biomeList);
        }

        ShaderParameterBool[] var9 = ShaderParameterBool.values();

        for (int var10 = 0; var10 < var9.length; ++var10)
        {
            ShaderParameterBool i = var9[var10];
            this.mapExpressions.put(i.getName(), i);
        }

        BiomeGenBase[] var11 = BiomeGenBase.getBiomeGenArray();

        for (int var12 = 0; var12 < var11.length; ++var12)
        {
            BiomeGenBase biome = var11[var12];

            if (biome != null)
            {
                String name = biome.biomeName.trim();
                name = "BIOME_" + name.toUpperCase().replace(' ', '_');
                int id = biome.biomeID;
                ConstantFloat expr = new ConstantFloat((float)id);
                this.registerExpression(name, expr);
            }
        }
    }

    public boolean registerExpression(String name, IExpression expr)
    {
        if (this.mapExpressions.containsKey(name))
        {
            SMCLog.warning("Expression already defined: " + name);
            return false;
        }
        else
        {
            this.mapExpressions.put(name, expr);
            return true;
        }
    }

    public IExpression getExpression(String name)
    {
        return (IExpression)this.mapExpressions.get(name);
    }

    public boolean hasExpression(String name)
    {
        return this.mapExpressions.containsKey(name);
    }
}
