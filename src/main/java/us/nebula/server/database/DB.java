/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.server.database;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.postgresql.util.DriverInfo;
import us.nebula.server.ServerProperties;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class DB
{
    private static final Logger LOGGER = LogManager.getLogger("DB");
    private static final String JBDC_URL = "jdbc:postgresql://%s:%s/%s";

    private static Connection CONNECTION;

    static
    {
        try
        {
            Class.forName("org.postgresql.Driver");
        } catch (final ClassNotFoundException e)
        {
            LOGGER.fatal("Postgres SQL driver not found", e);
            System.exit(-1);
        }
    }

    public static void connect()
    {
        LOGGER.debug("Postgres SQL Driver version: {}", DriverInfo.DRIVER_VERSION);
        try
        {
            LOGGER.info("Attempting to connect to Postgres SQL");
            final long startTime = System.currentTimeMillis();
            CONNECTION = DriverManager.getConnection(
                    String.format(JBDC_URL,
                            ServerProperties.get("postgres_host"),
                            ServerProperties.get("postgres_port"),
                            ServerProperties.get("postgres_database")),
                    ServerProperties.get("postgres_username"),
                    ServerProperties.get("postgres_password"));
            final long connectTime = System.currentTimeMillis();
            LOGGER.info("Connected to Postgres SQL in {}ms", connectTime - startTime);
        } catch (final SQLException e)
        {
            LOGGER.fatal("Failed to connect to database", e);
            System.exit(-1);
        }
    }

    public static void close()
    {
        if (CONNECTION == null)
        {
            LOGGER.warn("No connection open to database...");
            return;
        }
        LOGGER.info("Attempting to close database");
        try
        {
            CONNECTION.close();
            LOGGER.info("Closed database connection!");
        } catch (final SQLException e)
        {
            LOGGER.fatal("Failed to close database", e);
            System.exit(-1);
        }
    }

    public static boolean isPostgresEnabled()
    {
        return ServerProperties.get("postgres_enabled", "true")
                .equalsIgnoreCase("true");
    }

    private DB()
    {
        // no-op
    }
}
