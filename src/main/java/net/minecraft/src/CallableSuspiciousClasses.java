package net.minecraft.src;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Vector;
import java.util.concurrent.Callable;

class CallableSuspiciousClasses implements Callable
{
    final CrashReport theCrashReport;

    CallableSuspiciousClasses(CrashReport par1CrashReport)
    {
        this.theCrashReport = par1CrashReport;
    }

    public String callSuspiciousClasses() throws SecurityException, NoSuchFieldException, IllegalAccessException, IllegalArgumentException
    {
        StringBuilder var1 = new StringBuilder();
        ArrayList var3;

        try
        {
            Field var2 = ClassLoader.class.getDeclaredField("classes");
            var2.setAccessible(true);
            var3 = new ArrayList((Vector)var2.get(CrashReport.class.getClassLoader()));
        }
        catch (Exception ex)
        {
            return "";
        }

        boolean var4 = true;
        boolean var5 = !CrashReport.class.getCanonicalName().equals("net.minecraft.CrashReport");
        HashMap var6 = new HashMap();
        String var7 = "";
        Collections.sort(var3, new ComparatorClassSorter(this));
        Iterator var8 = var3.iterator();

        while (var8.hasNext())
        {
            Class var9 = (Class)var8.next();

            if (var9 != null)
            {
                String var10 = var9.getCanonicalName();

                if (var10 != null && !var10.startsWith("org.lwjgl.") && !var10.startsWith("paulscode.") && !var10.startsWith("org.bouncycastle.") && !var10.startsWith("argo.") && !var10.startsWith("com.jcraft.") && !var10.startsWith("com.fasterxml.") && !var10.startsWith("com.google.") && !var10.startsWith("joptsimple.") && !var10.startsWith("org.apache.") && !var10.equals("util.GLX"))
                {
                    if (var5)
                    {
                        if (var10.length() <= 3 || var10.equals("net.minecraft.client.main.Main") || var10.equals("net.minecraft.client.Minecraft") || var10.equals("net.minecraft.client.ClientBrandRetriever") || var10.equals("net.minecraft.server.MinecraftServer"))
                        {
                            continue;
                        }
                    }
                    else if (var10.startsWith("net.minecraft"))
                    {
                        continue;
                    }

                    Package var11 = var9.getPackage();
                    String var12 = var11 == null ? "" : var11.getName();

                    if (var6.containsKey(var12))
                    {
                        int var13 = ((Integer)var6.get(var12)).intValue();
                        var6.put(var12, Integer.valueOf(var13 + 1));

                        if (var13 == 3)
                        {
                            if (!var4)
                            {
                                var1.append(", ");
                            }

                            var1.append("...");
                            var4 = false;
                            continue;
                        }

                        if (var13 > 3)
                        {
                            continue;
                        }
                    }
                    else
                    {
                        var6.put(var12, Integer.valueOf(1));
                    }

                    if (!var7.equals(var12) && var7.length() > 0)
                    {
                        var1.append("], ");
                    }

                    if (!var4 && var7.equals(var12))
                    {
                        var1.append(", ");
                    }

                    if (!var7.equals(var12))
                    {
                        var1.append("[");
                        var1.append(var12);
                        var1.append(".");
                    }

                    var1.append(var9.getSimpleName());
                    var7 = var12;
                    var4 = false;
                }
            }
        }

        if (var4)
        {
            var1.append("No suspicious classes found.");
        }
        else
        {
            var1.append("]");
        }

        return var1.toString();
    }

    public Object call()
    {
        try
        {
            return this.callSuspiciousClasses();
        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
