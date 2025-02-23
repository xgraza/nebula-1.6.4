package us.nebula.launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.URLClassPath;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author xgraza
 * @since 02/22/25
 */
public final class Launcher
{
    /**
     * The MD5 MessageDigest used for generating checksums
     */
    private static final MessageDigest MESSAGE_DIGEST;

    private static final Logger LOGGER = LogManager.getLogger("Launcher");

    /**
     * The root directory where the client JAR, libraries, and other assets will be downloaded to
     */
    private static final File ROOT_DIRECTORY;

    /**
     * This computers hardware ID used for beta diff
     * TODO: use password for beta builds
     */
    private static final String USER_HWID;

    static
    {
        ROOT_DIRECTORY = new File(System.getProperty("user.home"), "nebula-client");
        if (!ROOT_DIRECTORY.exists())
        {
            if (!ROOT_DIRECTORY.mkdir())
            {
                throw new RuntimeException("Failed to create " + ROOT_DIRECTORY.getAbsolutePath());
            }
            LOGGER.info("Created {} successfully", ROOT_DIRECTORY.getAbsolutePath());
        }

        try
        {
            MESSAGE_DIGEST = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }

        USER_HWID = HWID.get();
    }

    /**
     * The main method for {@link Launcher}
     * This will either be called by a minecraft launcher or by normal JAR execution
     * @param args arguments passed through
     */
    public static void main(final String[] args)
    {
        // print build version
        LOGGER.info("Nebula Launcher Version: v{}+{}-{}/{}",
                BuildConfig.VERSION,
                BuildConfig.BUILD,
                BuildConfig.HASH,
                BuildConfig.BRANCH);

        LOGGER.info("Comparing client.jar checksum to server");
        if (needsUpdate())
        {
            LOGGER.info("JAR either does not exist or checksums do not match, downloading from server");
            downloadUpdate();
        } else
        {
            LOGGER.info("Up to date, launching client now");
        }
        launchGame(args);
    }

    private static void launchGame(final String[] args)
    {
        final File file = new File(ROOT_DIRECTORY, "client.jar");

        final URLClassLoader classLoader = (URLClassLoader) Launcher.class.getClassLoader();
        try
        {
            final Field field = classLoader.getClass().getDeclaredField("ucp");
            field.setAccessible(true);
            final URLClassPath ucp = (URLClassPath) field.get(classLoader);
            ucp.addURL(file.toURI().toURL());
        } catch (final IllegalAccessException | NoSuchFieldException | MalformedURLException e)
        {
            throw new RuntimeException(e);
        }

        try
        {
            final Class<?> buildConfigClass = Class.forName("lol.nebula.BuildConfig");
            final String version = (String) buildConfigClass.getField("VERSION").get(null);
            final String hash = (String) buildConfigClass.getField("HASH").get(null);
            final String branch = (String) buildConfigClass.getField("BRANCH").get(null);
            final int buildNumber = (int) buildConfigClass.getField("BUILD_NUMBER").get(null);
            LOGGER.info("Launching Nebula v{}+{}-{}/{}", version, buildNumber, hash, branch);
        } catch (ClassNotFoundException | NoSuchFieldException | IllegalAccessException e)
        {
            throw new RuntimeException("BuildConfig is missing?");
        }

        try
        {
            final Class<?> mcStartClass = Class.forName("net.minecraft.client.main.Main");
            final Method method = mcStartClass.getDeclaredMethod("main", String[].class);
            method.invoke(null, (Object) args);
        } catch (ClassNotFoundException | NoSuchMethodException | InvocationTargetException | IllegalAccessException e)
        {
            throw new RuntimeException(e);
        }
    }

    private static void downloadUpdate()
    {
        final File file = new File(ROOT_DIRECTORY, "client.jar");
        if (file.exists())
        {
            if (!file.delete())
            {
                throw new RuntimeException("failed to delete old client jar");
            }
            LOGGER.info("Deleted old client.jar");
        }
        LOGGER.info("Requesting & downloading update");
        final long startTime = System.nanoTime();
        new Connection("download")
                .addHeader("X-HWID", USER_HWID)
                .connect((stream) ->
                {
                    try (final FileOutputStream fos = new FileOutputStream(file))
                    {
                        int b;
                        while ((b = stream.read()) != -1)
                        {
                            fos.write(b);
                        }
                    } catch (final IOException e)
                    {
                        LOGGER.error("Failed to download client.jar from server");
                        throw new RuntimeException(e);
                    }
                });
        final long endTime = System.nanoTime();
        LOGGER.info("Downloaded client.jar from server in {}ms",
                (endTime - startTime) / 1000000.0);
    }

    private static boolean needsUpdate()
    {
        final File file = new File(ROOT_DIRECTORY, "client.jar");
        if (!file.exists())
        {
            return true;
        }
        final String checksum = getFileChecksum(file);
        final StringBuilder serverChecksumBuilder = new StringBuilder();
        new Connection("download_checksum")
            .addHeader("X-HWID", USER_HWID)
            .connect((stream) ->
            {
                try
                {
                    int b;
                    while ((b = stream.read()) != -1)
                    {
                        serverChecksumBuilder.append((char)b);
                    }
                } catch (final IOException e)
                {
                    throw new RuntimeException(e);
                }
            });
        return !serverChecksumBuilder.toString().equals(checksum);
    }

    private static String getFileChecksum(final File file)
    {
        MESSAGE_DIGEST.reset();
        try (final FileInputStream fis = new FileInputStream(file))
        {
            int i;
            while ((i = fis.read()) != -1) {
                MESSAGE_DIGEST.update((byte)i);
            }
        } catch (IOException e)
        {
            return null;
        }
        return Util.bytesToHex(MESSAGE_DIGEST.digest());
    }
}
