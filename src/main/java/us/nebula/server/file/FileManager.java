/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.server.file;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.*;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class FileManager
{
    public static final File DIRECTORY = new File(
            System.getProperty("user.dir"), "assets");
    private static final Logger LOGGER = LogManager.getLogger("File Manager");

    private static final Map<String, String> FILE_CHECKSUM_CACHE = new LinkedHashMap<>();
    private static final Map<String, File> FILE_NAME_CACHE = new LinkedHashMap<>();

    private static MessageDigest SHA256_DIGEST;

    static
    {
        try
        {
            SHA256_DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException e)
        {
            LOGGER.fatal("Failed to create SHA-256 digest", e);
            System.exit(-1);
        }
    }

    public static void init()
    {
        if (!DIRECTORY.exists())
        {
            LOGGER.warn("{} does not exist", DIRECTORY.getAbsoluteFile());
            if (!DIRECTORY.mkdir())
            {
                LOGGER.fatal("Failed to create {}", DIRECTORY);
                System.exit(-1);
                return;
            }
        }
        scanAssetDirectory(DIRECTORY);
    }

    public static String getChecksum(final String file)
    {
        return FILE_CHECKSUM_CACHE.get(file);
    }

    public static File getFile(final String file)
    {
        return FILE_NAME_CACHE.get(file);
    }

    private static void scanAssetDirectory(final File directory)
    {
        final File[] files = directory.listFiles();
        if (files == null)
        {
            return;
        }
        LOGGER.debug("Indexing {} files in {}", files.length, directory);
        for (final File file : files)
        {
            if (file.isDirectory())
            {
                scanAssetDirectory(file);
            } else
            {
                createChecksumFile(file);
                FILE_NAME_CACHE.put(file.getName(), file);
            }
        }
    }

    private static void createChecksumFile(final File file)
    {
        // don't hash checksum files lol
        if (file.getName().endsWith(".sha256"))
        {
            return;
        }
        final File sha256File = new File(file.getAbsoluteFile() + ".sha256");
        try (final OutputStream os = Files.newOutputStream(sha256File.toPath()))
        {
            final String checksum = checksum(file);
            if (checksum == null)
            {
                LOGGER.fatal("Checksum could not be generated for {}", file);
                System.exit(-1);
                return;
            }
            FILE_CHECKSUM_CACHE.put(file.getName(), checksum);
            final byte[] bytes = checksum.getBytes(StandardCharsets.UTF_8);
            os.write(bytes, 0, bytes.length);
            LOGGER.debug("Updated SHA-256 checksum for {}", file);
        } catch (final NullPointerException | IOException e)
        {
            LOGGER.fatal("Failed to create OutputStream for {}", file);
            System.exit(-1);
        }
    }

    public static String checksum(final File file)
    {
        byte[] digest;
        try (final InputStream is = new BufferedInputStream(Files.newInputStream(file.toPath())))
        {
            int b;
            while ((b = is.read()) != -1)
            {
                SHA256_DIGEST.update((byte) b);
            }
            digest = SHA256_DIGEST.digest();
        } catch (final IOException e)
        {
            LOGGER.fatal("Failed to create InputStream for {}", file);
            return null;
        }
        return sha256ToHex(digest);
    }

    public static String sha256ToHex(final byte[] digest)
    {
        final StringBuilder builder = new StringBuilder(digest.length * 2);
        for (final byte b : digest)
        {
            final String hex = Integer.toHexString(0xFF & b);
            if (hex.length() == 1)
            {
                builder.append("0");
            }
            builder.append(hex);
        }
        return builder.toString();
    }

    private FileManager()
    {
        // no-op
    }
}
