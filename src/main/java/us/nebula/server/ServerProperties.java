/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.server;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class ServerProperties
{
    public static final File ENV_FILE = new File(System.getProperty("user.dir"), ".env");

    private static final Properties PROPERTIES = new Properties();

    static
    {
        if (!ENV_FILE.exists())
        {
            throw new RuntimeException(ENV_FILE.getAbsolutePath() + " does not exist.");
        }
    }

    /**
     * Loads properties from the .env file
     *
     * @throws IOException if the {@link InputStream} for the .env file fails to open
     */
    public static void load() throws IOException
    {
        PROPERTIES.clear();
        try (final InputStream is = Files.newInputStream(ENV_FILE.toPath()))
        {
            PROPERTIES.load(is);
        }
    }

    /**
     * Gets a property value or throws an exception if it is not present
     *
     * @param key the property key
     * @return the property value
     * @throws RuntimeException if the property does not exist
     */
    public static String getOrThrow(final String key)
    {
        if (!PROPERTIES.containsKey(key))
        {
            throw new RuntimeException(".env must contain " + key);
        }
        return get(key, null);
    }

    /**
     * Gets a property value
     *
     * @param key the property key
     * @return the property value or null
     */
    public static String get(final String key)
    {
        return get(key, null);
    }

    /**
     * Gets a property value
     *
     * @param key          the property key
     * @param defaultValue the default value if the key does not exist
     * @return the property value or the default value
     */
    public static String get(final String key, final String defaultValue)
    {
        return PROPERTIES.getProperty(key, defaultValue);
    }

    private ServerProperties()
    {
        // no-op
    }
}
