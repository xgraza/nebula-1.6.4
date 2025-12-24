/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.Properties;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class Config
{
    private static final Logger LOGGER = LogManager.getLogger("Config");
    private static final File LAUNCHER_PROPERTIES_FILE = new File(
            Launcher.LAUNCHER_WORKING_DIR, "launcher.cfg");
    private static final Properties LAUNCHER_PROPERTIES = new Properties();

    static
    {
        if (!LAUNCHER_PROPERTIES_FILE.exists())
        {
            try
            {
                if (LAUNCHER_PROPERTIES_FILE.createNewFile())
                {
                    LOGGER.debug("Created {}",
                            LAUNCHER_PROPERTIES_FILE.getAbsolutePath());
                } else
                {
                    throw new RuntimeException("Failed to create "
                            + LAUNCHER_PROPERTIES_FILE.getAbsolutePath());
                }
            } catch (final IOException e)
            {
                LOGGER.error("Failed to create {}",
                        LAUNCHER_PROPERTIES_FILE.getAbsolutePath());
                throw new RuntimeException(e);
            }
        }
    }

    public static void init() throws IOException
    {
        try (final InputStream is = Files.newInputStream(LAUNCHER_PROPERTIES_FILE.toPath()))
        {
            LAUNCHER_PROPERTIES.load(is);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try
            {
                save();
            } catch (final IOException e)
            {
                LOGGER.fatal(e);
            }
        }, "Config Saver"));
    }

    public static void save() throws IOException
    {
        try (final OutputStream os = Files.newOutputStream(LAUNCHER_PROPERTIES_FILE.toPath()))
        {
            LAUNCHER_PROPERTIES.store(os, null);
        }
    }

    public static boolean exists(final String key)
    {
        return LAUNCHER_PROPERTIES.containsKey(key);
    }

    public static void set(final String key, final String value)
    {
        LAUNCHER_PROPERTIES.setProperty(key, value);
        try
        {
            save();
        } catch (final IOException e)
        {
            LOGGER.error("Failed to save property", e);
        }
    }

    public static void delete(final String key)
    {
        LAUNCHER_PROPERTIES.remove(key);
    }

    public static String get(final String key)
    {
        return LAUNCHER_PROPERTIES.getProperty(key);
    }

    private Config()
    {

    }
}
