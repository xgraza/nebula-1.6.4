/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.URLClassPath;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.MalformedURLException;
import java.net.URLClassLoader;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class Launcher
{
    private static final Logger LOGGER = LogManager.getLogger("Launcher");
    public static final File LAUNCHER_WORKING_DIR = new File(
            System.getProperty("user.home"), "nebula-launcher");

    static
    {
        if (!LAUNCHER_WORKING_DIR.exists() && !LAUNCHER_WORKING_DIR.mkdir())
        {
            throw new RuntimeException("Failed to create launcher directory at "
                    + LAUNCHER_WORKING_DIR.getAbsolutePath());
        }
    }

    private static String[] LAUNCH_ARGS;

    public static void main(final String[] args) throws IOException
    {
        LAUNCH_ARGS = args;
        printNebulaLauncherInfo();

        LOGGER.info("Reading config file");
        Config.init();

        if (!Config.exists("hide_gui") || !Config.exists("default_option"))
        {
            LOGGER.info("Showing version selection GUI");
            LauncherGUI.setLookAndFeel();
            LauncherGUI.create();
        } else
        {
            final LaunchVersion version = LaunchVersion.getVersion(
                    Config.get("default_option"));
            if (version == null)
            {
                Config.delete("default_option");
                throw new RuntimeException("Invalid type...");
            }
            launchVersion(version);
        }
    }

    public static void launchVersion(final LaunchVersion version)
    {
        if (LauncherGUI.INSTANCE != null)
        {
            LauncherGUI.INSTANCE.setVisible(false);
        }
        LOGGER.info("Attempting to launch {}", version);

        final File jarFile = new File(LAUNCHER_WORKING_DIR, version.getFileName());
        if (isChecksumInvalid(jarFile, version.getFileName()))
        {
            if (jarFile.exists() && !jarFile.delete())
            {
                LOGGER.warn("Couldn't delete original file, will just override.");
            }
            downloadAndRecheckChecksum(jarFile, version.getFileName());
        }

        LOGGER.info("Indexing libraries");
        final File librariesFolder = new File(LAUNCHER_WORKING_DIR, "libs");
        if (!librariesFolder.exists() && !librariesFolder.mkdir())
        {
            throw new RuntimeException("Failed to create libraries folder");
        }

        final JsonObject manifestObject = getClientManifest();
        if (manifestObject == null)
        {
            throw new RuntimeException("Failed to fetch manifest object");
        }

        final JsonArray clientLibrariesArray = manifestObject
                .get(version.toString()).getAsJsonObject()
                .get("libraries").getAsJsonArray();
        for (final JsonElement element : clientLibrariesArray)
        {
            final String name = element.getAsString();
            final File file = new File(librariesFolder, name);
            if (!file.exists() || isChecksumInvalid(file, name))
            {
                if (file.exists() && !file.delete())
                {
                    LOGGER.warn("Couldn't delete original file, will just override.");
                }
                downloadAndRecheckChecksum(file, name);
            }
            addToClasspath(file);
        }

        addToClasspath(jarFile);
        LOGGER.info("Starting minecraft");
        invokeMinecraft();
    }

    private static void invokeMinecraft()
    {
        try
        {
            final Class<?> minecraftClass = Class.forName("net.minecraft.client.main.Main");
            LOGGER.debug("Invoking class {}", minecraftClass);
            minecraftClass.getMethod("main", String[].class)
                    .invoke(null, (Object) LAUNCH_ARGS);
        } catch (final ClassNotFoundException
                       | InvocationTargetException
                       | IllegalAccessException
                       | NoSuchMethodException e)
        {
            LOGGER.fatal("Failed to invoke minecraft", e);
            throw new RuntimeException(e);
        }
        if (LauncherGUI.INSTANCE != null)
        {
            LOGGER.debug("Disposing of LauncherGUI instance");
            LauncherGUI.INSTANCE.dispose();
        }
    }

    private static void addToClasspath(final File file)
    {
        LOGGER.info("Adding {} to classpath", file);
        final URLClassLoader classLoader = (URLClassLoader) Launcher.class.getClassLoader();
        try
        {
            final Field field = classLoader.getClass().getDeclaredField("ucp");
            field.setAccessible(true);
            final URLClassPath ucp = (URLClassPath) field.get(classLoader);
            ucp.addURL(file.toURI().toURL());
        } catch (final IllegalAccessException | NoSuchFieldException | MalformedURLException exception)
        {
            throw new RuntimeException(exception);
        }
    }

    private static void downloadAndRecheckChecksum(final File jarFile,
                                                   final String fileName)
    {
        LOGGER.info("Checksum mismatch, downloading to {}", jarFile);
        final int responseCode = HTTP.connect("POST",
                "/download",
                fileName,
                null,
                HTTP.downloadFile(jarFile));
        if (responseCode != 200)
        {
            LOGGER.error("Failed to download {} from server (code {})", fileName, responseCode);
        }
        LOGGER.info("Downloaded, re-checking checksum for {}", fileName);
        if (isChecksumInvalid(jarFile, fileName))
        {
            throw new RuntimeException(
                    "Something is seriously wrong, please report to the developer!");
        }
    }

    private static JsonObject getClientManifest()
    {
        final AtomicReference<JsonObject> atomicJsonObject = new AtomicReference<>(null);
        HTTP.connect("GET",
                "/manifest",
                null,
                null,
                (is) ->
                {
                    final String content = HTTP.readFrom(is);
                    if (content == null)
                    {
                        return;
                    }
                    final JsonElement element = new JsonParser().parse(content);
                    if (!element.isJsonObject())
                    {
                        return;
                    }
                    atomicJsonObject.set(element.getAsJsonObject());
                });
        return atomicJsonObject.get();
    }

    private static boolean isChecksumInvalid(final File file, final String fileName)
    {
        if (!file.exists())
        {
            return true;
        }
        final String clientChecksum = SHA256Checksum.get(file);
        final AtomicReference<String> atomicServerChecksum = new AtomicReference<>("");
        final int statusCode = HTTP.connect("POST",
                "/checksum",
                fileName,
                null,
                (is) -> atomicServerChecksum.set(HTTP.readFrom(is)));
        if (statusCode != 200)
        {
            LOGGER.error("/checksum returned {} for {}", statusCode, fileName);
            return false;
        }
        return !Objects.equals(clientChecksum, atomicServerChecksum.get());
    }

    private static void printNebulaLauncherInfo()
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
                for (int i = 0; i < padding; ++i)
                {
                    builder.append(" ");
                }
            }
            LOGGER.info("\t{} -> {}", builder, debugInfoMap.get(key));
        }
    }
}
