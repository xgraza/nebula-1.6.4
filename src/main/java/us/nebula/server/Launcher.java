/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.server;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.nebula.server.database.DB;
import us.nebula.server.file.FileManager;
import us.nebula.server.server.NebulaHTTPServer;

import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class Launcher
{
    private static final Logger LOGGER = LogManager.getLogger("Launcher");
    private static final String ASCII_ART_LOCATION = "/ascii_art.txt";

    public static void main(final String[] args) throws IOException
    {
        printAsciiArt();
        printNebulaServerInfo();

        try
        {
            LOGGER.info("Loading properties from {}",
                    ServerProperties.ENV_FILE.getAbsoluteFile());
            ServerProperties.load();

            if (DB.isPostgresEnabled())
            {
                LOGGER.info("Connecting to Postgres SQL");
                DB.connect();
            } else
            {
                LOGGER.warn("POSTGRES SET TO DISABLED!!!");
            }

            LOGGER.info("Initializing file manager");
            FileManager.init();
            LOGGER.info("Creating HTTP server");
            NebulaHTTPServer.init();
        } catch (final Exception e)
        {
            LOGGER.fatal("Failed to launch Nebula Server", e);
            System.exit(-1);
        }

        Runtime.getRuntime().addShutdownHook(new Thread(() ->
        {
            LOGGER.debug("Closing database connection");
            DB.close();
        }, "Shutdown Thread"));
    }

    private static void printAsciiArt() throws IOException
    {
        try (final InputStream stream = Launcher.class.getResourceAsStream(ASCII_ART_LOCATION))
        {
            if (stream == null)
            {
                LOGGER.error("Failed to get resource " + ASCII_ART_LOCATION);
                return;
            }
            int b;
            while ((b = stream.read()) != -1)
            {
                System.out.print((char) b);
            }
        }
        System.out.println();
    }

    private static void printNebulaServerInfo()
    {
        LOGGER.info("Launching {}", BuildConfig.GROUP);
        final Map<String, String> debugInfoMap = new LinkedHashMap<>();
        debugInfoMap.put("Version", BuildConfig.VERSION);
        debugInfoMap.put("Build ID", BuildConfig.BUILD);
        debugInfoMap.put("Git Hash", BuildConfig.HASH);
        debugInfoMap.put("Git Branch", BuildConfig.BRANCH);
        debugInfoMap.put("Build Time", BuildConfig.BUILD_TIME);

        int maxLength = 0;
        for (final String key : debugInfoMap.keySet())
        {
            final int length = key.length();
            if (length > maxLength)
            {
                maxLength = length;
            }
        }

        for (final String key : debugInfoMap.keySet())
        {
            final StringBuilder builder = new StringBuilder(key);
            final int padding = maxLength - key.length();
            if (padding > 0)
            {
                builder.append(" ".repeat(padding));
            }
            LOGGER.info("\t{} -> {}", builder, debugInfoMap.get(key));
        }
    }
}
