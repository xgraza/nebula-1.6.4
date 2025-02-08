package us.nebula.launcher;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import sun.misc.URLClassPath;

import javax.net.ssl.HttpsURLConnection;
import java.io.*;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 * @author xgraza
 * @since 02/08/25
 */
@SuppressWarnings("all")
public final class Launcher
{
    private static final Logger LOGGER = LogManager.getLogger("Nebula-Launcher");

    private static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64; rv:134.0) Gecko/20100101 Firefox/134.0";
    private static final URL PASTEBIN_VERSION_URL;

    private static final File NEBULA_DIRECTORY = new File(
            System.getProperty("user.home"),
            "nebula-client");

    static
    {
        try
        {
            PASTEBIN_VERSION_URL = new URL("https://pastebin.com/raw/JAFSdcuk");
        } catch (final MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static void main(final String[] args) throws IOException
    {
        LOGGER.info("Launcher Version: {}+{}-{}/{}",
                BuildConfig.VERSION,
                BuildConfig.BUILD,
                BuildConfig.HASH,
                BuildConfig.BRANCH);

        boolean created = false;
        if (!NEBULA_DIRECTORY.exists())
        {
            if (!NEBULA_DIRECTORY.mkdir())
            {
                throw new RuntimeException("Could not create " + NEBULA_DIRECTORY);
            }
            created = true;
            LOGGER.info("Created {} successfully", NEBULA_DIRECTORY.getAbsolutePath());
        }

        final String currentVersion = readCurrentVersion();
        final String version = getNebulaVersion();
        if (created || (version != null && !version.isEmpty() && !version.equals(currentVersion)))
        {
            LOGGER.info("Downloading latest Nebula version");
            downloadNebulaResources();
        }

        if (version != null && version.equals(currentVersion))
        {
            LOGGER.info("All up to date! Launching Nebula");
        } else
        {
            LOGGER.info("Launching Nebula");
        }

        final URLClassLoader classLoader = (URLClassLoader) Launcher.class.getClassLoader();
        try
        {
            final File extractLocation = new File(NEBULA_DIRECTORY, "Nebula.jar");
            final Field field = classLoader.getClass().getDeclaredField("ucp");
            field.setAccessible(true);
            final URLClassPath ucp = (URLClassPath) field.get(classLoader);
            ucp.addURL(extractLocation.toURI().toURL());
        } catch (IllegalAccessException | NoSuchFieldException e)
        {
            throw new RuntimeException(e);
        }

        LOGGER.info("Invoking Minecraft main now..");
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

    private static String readCurrentVersion() throws IOException
    {
        final File versionFile = new File(NEBULA_DIRECTORY, "version.txt");
        if (!versionFile.exists())
        {
            return null;
        }
        final StringBuilder builder = new StringBuilder();
        try (final FileInputStream fis = new FileInputStream(versionFile))
        {
            int b;
            while ((b = fis.read()) != -1)
            {
                builder.append((char)b);
            }
        }
        return builder.toString();
    }

    /**
     * this code is so bad please ignore!
     * @throws IOException because
     */
    private static void downloadNebulaResources() throws IOException
    {
        // delete any old files
        final File versionFile = new File(NEBULA_DIRECTORY, "version.txt");
        final File extractLocation = new File(NEBULA_DIRECTORY, "Nebula.jar");
        if (versionFile.exists())
        {
            versionFile.delete();
        }
        if (extractLocation.exists())
        {
            extractLocation.delete();
        }

        // fetch the latest
        final JsonObject object = fetchJSON("https://api.github.com/repos/xgraza/nebula-1.6.4/releases/latest").getAsJsonObject();
        // extract needed data
        final String version = object.get("name").getAsString();
        final String downloadUrl = object
                .getAsJsonArray("assets")
                .get(0)
                .getAsJsonObject()
                .get("browser_download_url")
                .getAsString();

        // write to our version file yurrrr
        try (final FileOutputStream fos = new FileOutputStream(versionFile))
        {
            fos.write(version.getBytes(StandardCharsets.UTF_8));
        }

        final HttpsURLConnection connection = (HttpsURLConnection) new URL(downloadUrl).openConnection();
        connection.setReadTimeout(5000);
        connection.setRequestProperty("Accept", "application/x-zip-compressed");
        connection.setRequestProperty("Accept-Encoding", "gzip, deflate, br, zstd");
        connection.setRequestProperty("User-Agent", USER_AGENT);
        connection.setInstanceFollowRedirects(true);
        connection.connect();

        if (connection.getResponseCode() != 200)
        {
            throw new RuntimeException("failed to fetch nebula downloads...");
        }

        LOGGER.info("Downloading ZIP to directory");
        final File zipPath = new File(NEBULA_DIRECTORY, "tmp.zip");
        try (final InputStream is = connection.getInputStream())
        {
            Files.copy(is, zipPath.toPath());
        }
        LOGGER.info("Downloaded! Extracting Nebula.jar from release");
        try (final ZipFile zip = new ZipFile(zipPath))
        {
            final ZipEntry entry = zip.getEntry("Nebula.jar");
            try (final InputStream is = zip.getInputStream(entry))
            {
                final FileOutputStream fis = new FileOutputStream(extractLocation);
                int b;
                while ((b = is.read()) != -1)
                {
                    fis.write(b);
                }
                LOGGER.info("Extracted Nebula.jar to directory!");
                fis.close();
            }
        }
        if (!zipPath.delete())
        {
            LOGGER.error("Failed to delete temporary zip file");
        }
    }

    private static JsonElement fetchJSON(final String url) throws IOException
    {
        final HttpsURLConnection connection = (HttpsURLConnection) new URL(url).openConnection();
        connection.setReadTimeout(5_000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "application/vnd.github+json");
        connection.setRequestProperty("X-Github-Api-Version", "2022-11-28");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        connection.connect();

        if (connection.getResponseCode() != 200)
        {
            connection.disconnect();
            return null;
        }

        try (final InputStream is = connection.getInputStream())
        {
            final StringBuilder content = new StringBuilder();
            int i;
            while ((i = is.read()) != -1)
            {
                content.append((char)i);
            }
            return new JsonParser().parse(content.toString());
        } finally
        {
            connection.disconnect();
        }
    }

    private static String getNebulaVersion() throws IOException
    {
        final HttpsURLConnection connection = (HttpsURLConnection) PASTEBIN_VERSION_URL.openConnection();
        connection.setReadTimeout(5_000);
        connection.setRequestMethod("GET");
        connection.setRequestProperty("Accept", "plain/text");
        connection.setRequestProperty("User-Agent", USER_AGENT);

        connection.connect();

        if (connection.getResponseCode() != 200)
        {
            connection.disconnect();
            return null;
        }

        try (final InputStream is = connection.getInputStream())
        {
            final StringBuilder content = new StringBuilder();
            int i;
            while ((i = is.read()) != -1)
            {
                content.append((char)i);
            }
            return content.toString();
        } finally
        {
            connection.disconnect();
        }
    }
}
