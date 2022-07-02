package net.minecraft.src;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Profiler
{
    /** List of parent sections */
    private final List sectionList = new ArrayList();

    /** List of timestamps (System.nanoTime) */
    private final List timestampList = new ArrayList();

    /** Flag profiling enabled */
    public boolean profilingEnabled;

    /** Current profiling section */
    private String profilingSection = "";

    /** Profiling map */
    private final Map profilingMap = new HashMap();
    public boolean profilerGlobalEnabled = true;
    private boolean profilerLocalEnabled;
    private long startTickNano;
    public long timeTickNano;
    private long startUpdateChunksNano;
    public long timeUpdateChunksNano;

    public Profiler()
    {
        this.profilerLocalEnabled = this.profilerGlobalEnabled;
        this.startTickNano = 0L;
        this.timeTickNano = 0L;
        this.startUpdateChunksNano = 0L;
        this.timeUpdateChunksNano = 0L;
    }

    /**
     * Clear profiling.
     */
    public void clearProfiling()
    {
        this.profilingMap.clear();
        this.profilingSection = "";
        this.sectionList.clear();
        this.profilerLocalEnabled = this.profilerGlobalEnabled;
    }

    /**
     * Start section
     */
    public void startSection(String par1Str)
    {
        if (Config.getGameSettings().showDebugInfo)
        {
            if (this.startTickNano == 0L && par1Str.equals("tick"))
            {
                this.startTickNano = System.nanoTime();
            }

            if (this.startTickNano != 0L && par1Str.equals("preRenderErrors"))
            {
                this.timeTickNano = System.nanoTime() - this.startTickNano;
                this.startTickNano = 0L;
            }

            if (this.startUpdateChunksNano == 0L && par1Str.equals("updatechunks"))
            {
                this.startUpdateChunksNano = System.nanoTime();
            }

            if (this.startUpdateChunksNano != 0L && par1Str.equals("terrain"))
            {
                this.timeUpdateChunksNano = System.nanoTime() - this.startUpdateChunksNano;
                this.startUpdateChunksNano = 0L;
            }
        }

        if (this.profilerLocalEnabled)
        {
            if (this.profilingEnabled)
            {
                if (this.profilingSection.length() > 0)
                {
                    this.profilingSection = this.profilingSection + ".";
                }

                this.profilingSection = this.profilingSection + par1Str;
                this.sectionList.add(this.profilingSection);
                this.timestampList.add(Long.valueOf(System.nanoTime()));
            }
        }
    }

    /**
     * End section
     */
    public void endSection()
    {
        if (this.profilerLocalEnabled)
        {
            if (this.profilingEnabled)
            {
                long var1 = System.nanoTime();
                long var3 = ((Long)this.timestampList.remove(this.timestampList.size() - 1)).longValue();
                this.sectionList.remove(this.sectionList.size() - 1);
                long var5 = var1 - var3;

                if (this.profilingMap.containsKey(this.profilingSection))
                {
                    this.profilingMap.put(this.profilingSection, Long.valueOf(((Long)this.profilingMap.get(this.profilingSection)).longValue() + var5));
                }
                else
                {
                    this.profilingMap.put(this.profilingSection, Long.valueOf(var5));
                }

                if (var5 > 100000000L)
                {
                    System.out.println("Something\'s taking too long! \'" + this.profilingSection + "\' took aprox " + (double)var5 / 1000000.0D + " ms");
                }

                this.profilingSection = !this.sectionList.isEmpty() ? (String)this.sectionList.get(this.sectionList.size() - 1) : "";
            }
        }
    }

    /**
     * Get profiling data
     */
    public List getProfilingData(String par1Str)
    {
        this.profilerLocalEnabled = this.profilerGlobalEnabled;

        if (!this.profilerLocalEnabled)
        {
            return new ArrayList(Arrays.asList(new ProfilerResult[] {new ProfilerResult("root", 0.0D, 0.0D)}));
        }
        else if (!this.profilingEnabled)
        {
            return null;
        }
        else
        {
            long var2 = this.profilingMap.containsKey("root") ? ((Long)this.profilingMap.get("root")).longValue() : 0L;
            long var4 = this.profilingMap.containsKey(par1Str) ? ((Long)this.profilingMap.get(par1Str)).longValue() : -1L;
            ArrayList var6 = new ArrayList();

            if (par1Str.length() > 0)
            {
                par1Str = par1Str + ".";
            }

            long var7 = 0L;
            Iterator var9 = this.profilingMap.keySet().iterator();

            while (var9.hasNext())
            {
                String var10 = (String)var9.next();

                if (var10.length() > par1Str.length() && var10.startsWith(par1Str) && var10.indexOf(".", par1Str.length() + 1) < 0)
                {
                    var7 += ((Long)this.profilingMap.get(var10)).longValue();
                }
            }

            float var20 = (float)var7;

            if (var7 < var4)
            {
                var7 = var4;
            }

            if (var2 < var7)
            {
                var2 = var7;
            }

            Iterator var11 = this.profilingMap.keySet().iterator();
            String var12;

            while (var11.hasNext())
            {
                var12 = (String)var11.next();

                if (var12.length() > par1Str.length() && var12.startsWith(par1Str) && var12.indexOf(".", par1Str.length() + 1) < 0)
                {
                    long var13 = ((Long)this.profilingMap.get(var12)).longValue();
                    double var15 = (double)var13 * 100.0D / (double)var7;
                    double var17 = (double)var13 * 100.0D / (double)var2;
                    String var19 = var12.substring(par1Str.length());
                    var6.add(new ProfilerResult(var19, var15, var17));
                }
            }

            var11 = this.profilingMap.keySet().iterator();

            while (var11.hasNext())
            {
                var12 = (String)var11.next();
                this.profilingMap.put(var12, Long.valueOf(((Long)this.profilingMap.get(var12)).longValue() * 999L / 1000L));
            }

            if ((float)var7 > var20)
            {
                var6.add(new ProfilerResult("unspecified", (double)((float)var7 - var20) * 100.0D / (double)var7, (double)((float)var7 - var20) * 100.0D / (double)var2));
            }

            Collections.sort(var6);
            var6.add(0, new ProfilerResult(par1Str, 100.0D, (double)var7 * 100.0D / (double)var2));
            return var6;
        }
    }

    /**
     * End current section and start a new section
     */
    public void endStartSection(String par1Str)
    {
        if (this.profilerLocalEnabled)
        {
            this.endSection();
            this.startSection(par1Str);
        }
    }

    public String getNameOfLastSection()
    {
        return this.sectionList.size() == 0 ? "[UNKNOWN]" : (String)this.sectionList.get(this.sectionList.size() - 1);
    }
}
