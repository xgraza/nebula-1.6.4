/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.nebula.launcher.github.GithubAPIUtil;
import us.nebula.launcher.github.GithubOauthFlow;
import us.nebula.launcher.util.NebulaMetadata;
import us.nebula.launcher.util.Util;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.util.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class Wrapper
{
    public static final int LAUNCH_TYPE_STABLE = 0;
    public static final int LAUNCH_TYPE_LATEST = 1;

    private static final Logger LOGGER = LogManager.getLogger("Launcher");
    private static final String JAR_BASE_URL = "https://github.com/xgraza/nebula-1.7.2/raw/refs/heads/%s/dependencies/buildLibraries/%s";
    private static final String START_CLASS = "net.minecraft.client.main.Main";
    private static final File LIBRARIES_DIRECTORY = new File(
            LauncherMain.LAUNCHER_DIRECTORY, "libraries");

    private final Map<String, String> headers = new LinkedHashMap<>();

    public void launch(final int type)
    {
        if (type != LAUNCH_TYPE_STABLE && type != LAUNCH_TYPE_LATEST)
        {
            LauncherMain.launchGUI();
            return;
        }
        LOGGER.debug("Launch type: {}", type);
        if (type == LAUNCH_TYPE_LATEST)
        {
            final GithubOauthFlow githubOauthFlow = new GithubOauthFlow();
            final String accessToken = githubOauthFlow.startVerification();
            headers.put("Authorization", "Bearer " + accessToken);
        }
        try
        {
            NebulaMetadata.fetch();
            LOGGER.debug("Fetched metadata successfully");
        } catch (final Exception exception)
        {
            LOGGER.error(exception);
            throw new RuntimeException(exception);
        }

        LOGGER.info("Indexing downloaded libraries...");
        final List<String> indexedLibraries = indexLibrariesToRedownload();
        if (!indexedLibraries.isEmpty())
        {
            downloadLibraries(indexedLibraries);
        }

        LOGGER.info("Done! Indexing Minecraft client jarmod...");
        final File file = new File(LauncherMain.LAUNCHER_DIRECTORY,
                type == LAUNCH_TYPE_STABLE
                        ? "nebula-stable.jar"
                        : "nebula-latest.jar");
        if (!file.exists() /*|| !checkClientJarmodChecksum(type, file)*/)
        {
            LOGGER.info("Client jarmod checksum is invalid OR does not exist. Redownloading...");
            downloadClientJarmod(type, file);
        }
        LOGGER.debug("Adding {} to classpath", file);
        Util.addToClasspath(file);
        LOGGER.info("Starting minecraft...");
        startClient();
    }

    private void startClient()
    {
        LOGGER.info("Invoking Main#main() with args:");
        for (final String arg : LauncherMain.ARGS)
        {
            LOGGER.info("\t{}", arg);
        }
        try
        {
            final Class<?> mainClass = Class.forName(START_CLASS);
            mainClass.getMethod("main", String[].class)
                    .invoke(null, (Object) LauncherMain.ARGS);
        } catch (final Exception exception)
        {
            LOGGER.error(exception);
            System.exit(-1);
        }
    }

    private boolean checkClientJarmodChecksum(final int type, final File file)
    {
        return false;
    }

    private void downloadClientJarmod(final int type, final File file)
    {
        // stable is directly from the releases tab
        // latest is from the most recent successful action runner result
        final String url = type == LAUNCH_TYPE_STABLE ? "" : GithubAPIUtil.getLatestReleaseURL();
        final File tempZip = new File(LauncherMain.LAUNCHER_DIRECTORY, "tmp.zip");
        try
        {
            LOGGER.info(url);
            Util.downloadFile(url, headers, tempZip);
        } catch (final IOException exception)
        {
            LOGGER.error(exception);
            System.exit(-1);
        }

        try
        {
            unpackJarFromZipStream(new ZipInputStream(Files.newInputStream(tempZip.toPath())), file);
        } catch (final IOException exception)
        {
            LOGGER.error(exception);
            System.exit(-1);
        }
        tempZip.delete();
    }

    private void unpackJarFromZipStream(final ZipInputStream stream, final File file)
            throws IOException
    {
        ZipEntry entry;
        while ((entry = stream.getNextEntry()) != null)
        {
            // there will only be one jar file
            // TODO; specific name checks?
            if (!entry.getName().endsWith(".jar"))
            {
                stream.closeEntry();
                continue;
            }

            try (final OutputStream os = Files.newOutputStream(file.toPath()))
            {
                int b;
                while ((b = stream.read()) != -1)
                {
                    os.write(b);
                }
            }
            // once we're done, break the loop and close the zip file
            stream.closeEntry();
            stream.close();
            return;
        }
    }

    private void downloadLibraries(final List<String> libraries)
    {
        LOGGER.info("Downloading {} librarie(s)", libraries.size());
        for (final String library : libraries)
        {
            final File file = new File(LIBRARIES_DIRECTORY, library);
            try
            {
                LOGGER.info("Downloading {}", library);
                Util.downloadFile(String.format(JAR_BASE_URL,
                                NebulaMetadata.BRANCH,
                                library),
                        null, file);
                LOGGER.info("Downloaded {} successfully", library);
            } catch (final IOException exception)
            {
                LOGGER.error(exception);
                throw new RuntimeException(exception);
            }
        }
    }

    private List<String> indexLibrariesToRedownload()
    {
        if (!LIBRARIES_DIRECTORY.exists())
        {
            if (!LIBRARIES_DIRECTORY.mkdir())
            {
                LOGGER.error("Failed to create directory at {}", LIBRARIES_DIRECTORY);
                throw new RuntimeException("");
            }
            LOGGER.debug("Created launcher directory at {}", LIBRARIES_DIRECTORY);
            // return expected libraries as this directory didnt exist..
            return NebulaMetadata.LIBRARIES;
        }
        final List<String> librariesToDownloadList = new LinkedList<>();
        for (final String library : NebulaMetadata.LIBRARIES)
        {
            final File libFile = new File(LIBRARIES_DIRECTORY, library);
            String checksum = null;
            if (libFile.exists())
            {
                checksum = Util.getChecksum(libFile);
            }
            final String officialChecksum = GithubAPIUtil.getGithubChecksum(library);
            LOGGER.debug("{} -> (Local: {}, Server: {})", library, checksum, officialChecksum);
            if (!Objects.equals(officialChecksum, checksum))
            {
                if (!libFile.delete())
                {
                    LOGGER.debug("Failed to delete library {}", libFile);
                }
                LOGGER.warn("Checksums for {} did not match, will redownload...", library);
                librariesToDownloadList.add(library);
            } else
            {
                LOGGER.debug("Adding {} to classpath", libFile);
                Util.addToClasspath(libFile);
            }
        }
        return librariesToDownloadList;
    }
}
