package net.minecraft.src;

import com.google.common.base.Splitter;
import com.google.common.collect.Iterables;
import com.google.common.collect.Maps;
import java.io.IOException;
import java.io.InputStream;
import java.util.IllegalFormatException;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
import org.apache.commons.io.Charsets;
import org.apache.commons.io.IOUtils;

public class Locale
{
    /** Splits on "=" */
    private static final Splitter splitter = Splitter.on('=').limit(2);
    private static final Pattern field_135031_c = Pattern.compile("%(\\d+\\$)?[\\d\\.]*[df]");
    Map field_135032_a = Maps.newHashMap();
    private boolean field_135029_d;

    /**
     * par2 is a list of languages. For each language $L and domain $D, attempts to load the resource $D:lang/$L.lang
     */
    public synchronized void loadLocaleDataFiles(ResourceManager par1ResourceManager, List par2List)
    {
        this.field_135032_a.clear();
        Iterator var3 = par2List.iterator();

        while (var3.hasNext())
        {
            String var4 = (String)var3.next();
            String var5 = String.format("lang/%s.lang", new Object[] {var4});
            Iterator var6 = par1ResourceManager.getResourceDomains().iterator();

            while (var6.hasNext())
            {
                String var7 = (String)var6.next();

                try
                {
                    this.loadLocaleData(par1ResourceManager.getAllResources(new ResourceLocation(var7, var5)));
                }
                catch (IOException var9)
                {
                    ;
                }
            }
        }

        this.checkUnicode();
    }

    public boolean isUnicode()
    {
        return this.field_135029_d;
    }

    private void checkUnicode()
    {
        this.field_135029_d = false;
        Iterator var1 = this.field_135032_a.values().iterator();

        while (var1.hasNext())
        {
            String var2 = (String)var1.next();

            for (int var3 = 0; var3 < var2.length(); ++var3)
            {
                if (var2.charAt(var3) >= 256)
                {
                    this.field_135029_d = true;
                    break;
                }
            }
        }
    }

    /**
     * par1 is a list of Resources
     */
    private void loadLocaleData(List par1List) throws IOException
    {
        Iterator var2 = par1List.iterator();

        while (var2.hasNext())
        {
            Resource var3 = (Resource)var2.next();
            this.loadLocaleData(var3.getInputStream());
        }
    }

    private void loadLocaleData(InputStream par1InputStream) throws IOException
    {
        Iterator var2 = IOUtils.readLines(par1InputStream, Charsets.UTF_8).iterator();

        while (var2.hasNext())
        {
            String var3 = (String)var2.next();

            if (!var3.isEmpty() && var3.charAt(0) != 35)
            {
                String[] var4 = (String[])Iterables.toArray(splitter.split(var3), String.class);

                if (var4 != null && var4.length == 2)
                {
                    String var5 = var4[0];
                    String var6 = field_135031_c.matcher(var4[1]).replaceAll("%$1s");
                    this.field_135032_a.put(var5, var6);
                }
            }
        }
    }

    /**
     * Returns the translation, or the key itself if the key could not be translated.
     */
    private String translateKeyPrivate(String par1Str)
    {
        String var2 = (String)this.field_135032_a.get(par1Str);
        return var2 == null ? par1Str : var2;
    }

    /**
     * Returns the translation, or the key itself if the key could not be translated.
     */
    public String translateKey(String par1Str)
    {
        return this.translateKeyPrivate(par1Str);
    }

    /**
     * Calls String.format(translateKey(key), params)
     */
    public String formatMessage(String par1Str, Object[] par2ArrayOfObj)
    {
        String var3 = this.translateKeyPrivate(par1Str);

        try
        {
            return String.format(var3, par2ArrayOfObj);
        }
        catch (IllegalFormatException var5)
        {
            return "Format error: " + var3;
        }
    }
}
