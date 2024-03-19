package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class FieldLocatorTypes implements IFieldLocator
{
    private Field field = null;

    public FieldLocatorTypes(Class cls, Class[] preTypes, Class type, Class[] postTypes, String errorName)
    {
        Field[] fs = cls.getDeclaredFields();
        ArrayList types = new ArrayList();

        for (int typesMatch = 0; typesMatch < fs.length; ++typesMatch)
        {
            Field index = fs[typesMatch];
            types.add(index.getType());
        }

        ArrayList var12 = new ArrayList();
        var12.addAll(Arrays.asList(preTypes));
        var12.add(type);
        var12.addAll(Arrays.asList(postTypes));
        int var13 = Collections.indexOfSubList(types, var12);

        if (var13 < 0)
        {
            Config.warn("Field not found: " + errorName);
        }
        else
        {
            int index2 = Collections.indexOfSubList(types.subList(var13 + 1, types.size()), var12);

            if (index2 >= 0)
            {
                Config.warn("More than one match found for field: " + errorName);
            }
            else
            {
                int indexField = var13 + preTypes.length;
                this.field = fs[indexField];
            }
        }
    }

    public Field getField()
    {
        return this.field;
    }
}
