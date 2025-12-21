/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher.util;

import com.google.gson.JsonElement;
import sun.misc.URLClassPath;

import java.io.*;
import java.lang.reflect.Field;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author xgraza
 * @since 12/20/25
 */
public final class Util
{
    private static final int READ_CONNECT_TIMEOUT = 5_000;
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

    public static void addToClasspath(final File file)
    {
        final URLClassLoader classLoader = (URLClassLoader) Util.class.getClassLoader();
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

    public static String getChecksum(final File file)
    {
        byte[] digestBytes;
        try (final BufferedInputStream is = new BufferedInputStream(Files.newInputStream(file.toPath())))
        {
            int b;
            while ((b = is.read()) != -1)
            {
                SHA256_DIGEST.update((byte) b);
            }
            digestBytes = SHA256_DIGEST.digest();
        } catch (IOException e)
        {
            throw new RuntimeException(e);
        }
        if (digestBytes == null)
        {
            return null;
        }
        return sha256ToHex(digestBytes);
    }

    public static String sha256ToHex(final byte[] bytes)
    {
        final StringBuilder builder = new StringBuilder();
        for (final byte b : bytes)
        {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }

    public static void downloadFile(final String url, final File file) throws IOException
    {
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setReadTimeout(READ_CONNECT_TIMEOUT);
        connection.setConnectTimeout(READ_CONNECT_TIMEOUT);
        connection.setRequestMethod("GET");
        connection.setInstanceFollowRedirects(true);

        connection.connect();

        if (connection.getResponseCode() != 200)
        {
            throw new RuntimeException(connection.getResponseCode()
                    + " -> " + connection.getResponseMessage());
        }

        try (final FileOutputStream fos = new FileOutputStream(file))
        {
            final InputStream is = connection.getInputStream();
            int b;
            while ((b = is.read()) != -1)
            {
                fos.write(b);
            }
            is.close();
        }
    }

    public static String makeConnection(final String method,
                                        final String url,
                                        final Object body) throws IOException
    {
        final HttpURLConnection connection = (HttpURLConnection) new URL(url).openConnection();
        connection.setReadTimeout(READ_CONNECT_TIMEOUT);
        connection.setConnectTimeout(READ_CONNECT_TIMEOUT);
        connection.setRequestMethod(method.toUpperCase());
        connection.setInstanceFollowRedirects(true);

        if (body != null)
        {
            connection.setDoOutput(true);
        }

        if (body != null)
        {
            // attempt to infer content type
            if (body instanceof JsonElement)
            {
                connection.setRequestProperty("Content-Type", "application/json");
            } else
            {
                connection.setRequestProperty("Content-Type", "text/plain");
            }

            final String content = body.toString();
            final byte[] bytes = content.getBytes(StandardCharsets.UTF_8);
            final OutputStream os = connection.getOutputStream();
            os.write(bytes, 0, bytes.length);
        }

        connection.connect();

        if (connection.getResponseCode() == 200)
        {
            final StringBuilder builder = new StringBuilder();
            final InputStream is = connection.getInputStream();
            int b;
            while ((b = is.read()) != -1)
            {
                builder.append((char) b);
            }
            return builder.toString();
        }
        throw new RuntimeException(connection.getResponseCode()
                + " -> " + connection.getResponseMessage());
    }

    private Util()
    {
        // no-op
    }
}
