package net.minecraft.src;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class ReflectorMethod
{
    private ReflectorClass reflectorClass;
    private String targetMethodName;
    private Class[] targetMethodParameterTypes;
    private boolean checked;
    private Method targetMethod;

    public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName)
    {
        this(reflectorClass, targetMethodName, (Class[])null, false);
    }

    public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName, Class[] targetMethodParameterTypes)
    {
        this(reflectorClass, targetMethodName, targetMethodParameterTypes, false);
    }

    public ReflectorMethod(ReflectorClass reflectorClass, String targetMethodName, Class[] targetMethodParameterTypes, boolean lazyResolve)
    {
        this.reflectorClass = null;
        this.targetMethodName = null;
        this.targetMethodParameterTypes = null;
        this.checked = false;
        this.targetMethod = null;
        this.reflectorClass = reflectorClass;
        this.targetMethodName = targetMethodName;
        this.targetMethodParameterTypes = targetMethodParameterTypes;

        if (!lazyResolve)
        {
            Method m = this.getTargetMethod();
        }
    }

    public Method getTargetMethod()
    {
        if (this.checked)
        {
            return this.targetMethod;
        }
        else
        {
            this.checked = true;
            Class cls = this.reflectorClass.getTargetClass();

            if (cls == null)
            {
                return null;
            }
            else
            {
                try
                {
                    if (this.targetMethodParameterTypes == null)
                    {
                        Method[] e = getMethods(cls, this.targetMethodName);

                        if (e.length <= 0)
                        {
                            Config.log("(Reflector) Method not present: " + cls.getName() + "." + this.targetMethodName);
                            return null;
                        }

                        if (e.length > 1)
                        {
                            Config.warn("(Reflector) More than one method found: " + cls.getName() + "." + this.targetMethodName);

                            for (int i = 0; i < e.length; ++i)
                            {
                                Method m = e[i];
                                Config.warn("(Reflector)  - " + m);
                            }

                            return null;
                        }

                        this.targetMethod = e[0];
                    }
                    else
                    {
                        this.targetMethod = getMethod(cls, this.targetMethodName, this.targetMethodParameterTypes);
                    }

                    if (this.targetMethod == null)
                    {
                        Config.log("(Reflector) Method not present: " + cls.getName() + "." + this.targetMethodName);
                        return null;
                    }
                    else
                    {
                        this.targetMethod.setAccessible(true);
                        return this.targetMethod;
                    }
                }
                catch (Throwable var5)
                {
                    var5.printStackTrace();
                    return null;
                }
            }
        }
    }

    public boolean exists()
    {
        return this.checked ? this.targetMethod != null : this.getTargetMethod() != null;
    }

    public Class getReturnType()
    {
        Method tm = this.getTargetMethod();
        return tm == null ? null : tm.getReturnType();
    }

    public void deactivate()
    {
        this.checked = true;
        this.targetMethod = null;
    }

    public static Method getMethod(Class cls, String methodName, Class[] paramTypes)
    {
        Method[] ms = cls.getDeclaredMethods();

        for (int i = 0; i < ms.length; ++i)
        {
            Method m = ms[i];

            if (m.getName().equals(methodName))
            {
                Class[] types = m.getParameterTypes();

                if (Reflector.matchesTypes(paramTypes, types))
                {
                    return m;
                }
            }
        }

        return null;
    }

    public static Method[] getMethods(Class cls, String methodName)
    {
        ArrayList listMethods = new ArrayList();
        Method[] ms = cls.getDeclaredMethods();

        for (int methods = 0; methods < ms.length; ++methods)
        {
            Method m = ms[methods];

            if (m.getName().equals(methodName))
            {
                listMethods.add(m);
            }
        }

        Method[] var6 = (Method[])((Method[])listMethods.toArray(new Method[listMethods.size()]));
        return var6;
    }
}
