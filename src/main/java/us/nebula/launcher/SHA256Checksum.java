/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class SHA256Checksum
{
    private static final MessageDigest SHA256_DIGEST;

    static
    {
        try
        {
            SHA256_DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (final NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String get(final File file)
    {
        if (file == null || !file.exists())
        {
            return null;
        }
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

    private SHA256Checksum()
    {
        // no-op
    }
}
