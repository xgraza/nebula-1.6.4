package net.minecraft.client;

import ez.nebula.client.util.math.MathUtil;
import net.minecraft.util.ResourceLocation;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author xgraza
 * @since 3/14/26
 */
public final class SplashTextProvider
{
    private static final Minecraft MC = Minecraft.getMinecraft();
    private static final Map<String, List<String>> SPLASH_TEXT_MAP = new HashMap<>();
    private static final String DEFAULT_SPLASH_TEXT = "missingno";

    public static String getRandomSplashText(final String domain)
    {
        final List<String> splashTextList = SPLASH_TEXT_MAP.get(domain);
        if (splashTextList == null || splashTextList.isEmpty())
        {
            return DEFAULT_SPLASH_TEXT;
        }
        String splashText;
        do
        {
            splashText = splashTextList.get(MathUtil.RNG.nextInt(splashTextList.size()));
        }
        while (splashText.hashCode() == 125780783);
        return splashText;
    }

    public static void addSplashTextProvider(final ResourceLocation location)
    {
        if (SPLASH_TEXT_MAP.containsKey(location.getResourceDomain()))
        {
            return;
        }
        final List<String> splashTextList = new ArrayList<>();
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(
                MC.getResourceManager().getResource(location).getInputStream())))
        {
            String line;
            while ((line = reader.readLine()) != null)
            {
                splashTextList.add(line.trim());
            }
        } catch (final IOException e)
        {
            e.printStackTrace();
        }
        SPLASH_TEXT_MAP.put(location.getResourceDomain(), splashTextList);
    }
}
