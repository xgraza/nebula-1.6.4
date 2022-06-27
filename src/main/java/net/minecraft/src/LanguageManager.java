package net.minecraft.src;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.SortedSet;

public class LanguageManager implements ResourceManagerReloadListener
{
    private final MetadataSerializer field_135047_b;
    private String currentLanguage;
    protected static final Locale currentLocale = new Locale();
    private Map languageMap = Maps.newHashMap();

    public LanguageManager(MetadataSerializer par1MetadataSerializer, String par2Str)
    {
        this.field_135047_b = par1MetadataSerializer;
        this.currentLanguage = par2Str;
        I18n.setLocale(currentLocale);
    }

    public void parseLanguageMetadata(List par1List)
    {
        this.languageMap.clear();
        Iterator var2 = par1List.iterator();

        while (var2.hasNext())
        {
            ResourcePack var3 = (ResourcePack)var2.next();

            try
            {
                LanguageMetadataSection var4 = (LanguageMetadataSection)var3.getPackMetadata(this.field_135047_b, "language");

                if (var4 != null)
                {
                    Iterator var5 = var4.getLanguages().iterator();

                    while (var5.hasNext())
                    {
                        Language var6 = (Language)var5.next();

                        if (!this.languageMap.containsKey(var6.getLanguageCode()))
                        {
                            this.languageMap.put(var6.getLanguageCode(), var6);
                        }
                    }
                }
            }
            catch (RuntimeException var7)
            {
                Minecraft.getMinecraft().getLogAgent().logWarningException("Unable to parse metadata section of resourcepack: " + var3.getPackName(), var7);
            }
            catch (IOException var8)
            {
                Minecraft.getMinecraft().getLogAgent().logWarningException("Unable to parse metadata section of resourcepack: " + var3.getPackName(), var8);
            }
        }
    }

    public void onResourceManagerReload(ResourceManager par1ResourceManager)
    {
        ArrayList var2 = Lists.newArrayList(new String[] {"en_US"});

        if (!"en_US".equals(this.currentLanguage))
        {
            var2.add(this.currentLanguage);
        }

        currentLocale.loadLocaleDataFiles(par1ResourceManager, var2);
        StringTranslate.func_135063_a(currentLocale.field_135032_a);
    }

    public boolean isCurrentLocaleUnicode()
    {
        return currentLocale.isUnicode();
    }

    public boolean isCurrentLanguageBidirectional()
    {
        return this.getCurrentLanguage().isBidirectional();
    }

    public void setCurrentLanguage(Language par1Language)
    {
        this.currentLanguage = par1Language.getLanguageCode();
    }

    public Language getCurrentLanguage()
    {
        return this.languageMap.containsKey(this.currentLanguage) ? (Language)this.languageMap.get(this.currentLanguage) : (Language)this.languageMap.get("en_US");
    }

    public SortedSet getLanguages()
    {
        return Sets.newTreeSet(this.languageMap.values());
    }
}
