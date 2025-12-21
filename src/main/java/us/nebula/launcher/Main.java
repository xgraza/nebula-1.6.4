/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.nebula.launcher.gui.GUI;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Properties;

/**
 * @author xgraza
 * @since 12/20/25
 */
public final class Main
{
    public static final File LAUNCHER_DIRECTORY = Paths.get(System.getProperty("user.home"))
            .resolve("nebula_launcher")
            .toFile();
    public static final Properties LAUNCHER_PROPERTIES = new Properties();
    public static final Wrapper WRAPPER = new Wrapper();
    public static GUI GUI_INSTANCE;

    private static final Logger LOGGER = LogManager.getLogger("Main");
    private static final File OPTIONS_FILE = new File(LAUNCHER_DIRECTORY, "launcher.properties");

    public static void main(String[] args) throws Exception
    {
        if (!LAUNCHER_DIRECTORY.exists())
        {
            if (!LAUNCHER_DIRECTORY.mkdir())
            {
                LOGGER.error("Failed to create directory at {}", LAUNCHER_DIRECTORY);
                throw new RuntimeException("");
            }
            LOGGER.debug("Created launcher directory at {}", LAUNCHER_DIRECTORY);
        }
        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            try
            {
                saveProperties();
            } catch (final IOException exception)
            {
                LOGGER.error(exception);
            }
        }, "Nebula Launcher Saver"));

        loadProperties();
        if (shouldLaunchGUI())
        {
            launchGUI();
            return;
        }
        WRAPPER.launch(getLaunchType());
    }

    public static void saveProperties() throws IOException
    {
        if (!OPTIONS_FILE.exists() && !OPTIONS_FILE.createNewFile())
        {
            LOGGER.error("Failed to create file {}", OPTIONS_FILE);
            return;
        }
        try (final OutputStream os = Files.newOutputStream(OPTIONS_FILE.toPath()))
        {
            LAUNCHER_PROPERTIES.store(os, null);
        }
    }

    private static void loadProperties() throws IOException
    {
        LOGGER.debug("Loading properties from {}", OPTIONS_FILE);
        if (OPTIONS_FILE.exists())
        {
            try (final InputStream is = Files.newInputStream(OPTIONS_FILE.toPath()))
            {
                LAUNCHER_PROPERTIES.load(is);
            }
        } else
        {
            if (!OPTIONS_FILE.createNewFile())
            {
                LOGGER.error("Failed to create file {}", OPTIONS_FILE);
                throw new RuntimeException("Failed to create file " + OPTIONS_FILE);
            }
        }
    }

    static void launchGUI()
    {
        if (GUI_INSTANCE != null)
        {
            return;
        }
        try
        {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (final Exception exception)
        {
            LOGGER.error(exception);
        }
        SwingUtilities.invokeLater(() ->
                GUI_INSTANCE = new GUI());
    }

    private static int getLaunchType()
    {
        if (LAUNCHER_PROPERTIES.containsKey("type"))
        {
            final String value = LAUNCHER_PROPERTIES.getProperty("type");
            try
            {
                return Integer.parseInt(value);
            } catch (final Exception e)
            {
                return -1;
            }
        }
        return -1;
    }

    private static boolean shouldLaunchGUI()
    {
        if (LAUNCHER_PROPERTIES.containsKey("ask"))
        {
            return LAUNCHER_PROPERTIES.getProperty("ask").equals("true");
        }
        return true;
    }
}
