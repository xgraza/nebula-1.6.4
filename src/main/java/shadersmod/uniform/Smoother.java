package shadersmod.uniform;

import net.minecraft.src.SmoothFloat;

import java.util.HashMap;
import java.util.Map;

public class Smoother
{
    private static final Map<Integer, SmoothFloat> mapSmoothValues = new HashMap();

    public static float getSmoothValue(int id, float value, float timeFadeUpSec, float timeFadeDownSec)
    {
        Map var4 = mapSmoothValues;

        synchronized (mapSmoothValues)
        {
            Integer key = Integer.valueOf(id);
            SmoothFloat sf = mapSmoothValues.get(key);

            if (sf == null)
            {
                sf = new SmoothFloat(value, timeFadeUpSec, timeFadeDownSec);
                mapSmoothValues.put(key, sf);
            }

            float valueSmooth = sf.getSmoothValue(value);
            return valueSmooth;
        }
    }

    public static void reset()
    {
        Map var0 = mapSmoothValues;

        synchronized (mapSmoothValues)
        {
            mapSmoothValues.clear();
        }
    }
}
