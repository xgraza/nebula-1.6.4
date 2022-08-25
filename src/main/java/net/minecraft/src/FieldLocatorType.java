package net.minecraft.src;

import java.lang.reflect.Field;

public class FieldLocatorType implements IFieldLocator
{
    private ReflectorClass reflectorClass;
    private Class targetFieldType;
    private int targetFieldIndex;

    public FieldLocatorType(ReflectorClass reflectorClass, Class targetFieldType)
    {
        this(reflectorClass, targetFieldType, 0);
    }

    public FieldLocatorType(ReflectorClass reflectorClass, Class targetFieldType, int targetFieldIndex)
    {
        this.reflectorClass = null;
        this.targetFieldType = null;
        this.reflectorClass = reflectorClass;
        this.targetFieldType = targetFieldType;
        this.targetFieldIndex = targetFieldIndex;
    }

    public Field getField()
    {
        Class cls = this.reflectorClass.getTargetClass();

        if (cls == null)
        {
            return null;
        }
        else
        {
            try
            {
                Field[] e = cls.getDeclaredFields();
                int fieldIndex = 0;

                for (int i = 0; i < e.length; ++i)
                {
                    Field field = e[i];

                    if (field.getType() == this.targetFieldType)
                    {
                        if (fieldIndex == this.targetFieldIndex)
                        {
                            field.setAccessible(true);
                            return field;
                        }

                        ++fieldIndex;
                    }
                }

                Config.log("(Reflector) Field not present: " + cls.getName() + ".(type: " + this.targetFieldType + ", index: " + this.targetFieldIndex + ")");
                return null;
            }
            catch (SecurityException var6)
            {
                var6.printStackTrace();
                return null;
            }
            catch (Throwable var7)
            {
                var7.printStackTrace();
                return null;
            }
        }
    }
}
