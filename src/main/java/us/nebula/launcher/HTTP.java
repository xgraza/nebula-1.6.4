/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.launcher;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class HTTP
{
    private static final Logger LOGGER = LogManager.getLogger("HTTP");
    private static final String BASE_URL = "http://localhost:8080/%s";
    private static final int TIMEOUT_LENGTH = 10_000;

    public static int connect(final String method,
                              final String endpoint,
                              final String body,
                              final Map<String, String> headers,
                              final Consumer<InputStream> runnable)
    {
        try
        {
            final HttpURLConnection connection = (HttpURLConnection) new URL(
                    String.format(BASE_URL, fixEndpoint(endpoint))).openConnection();
            connection.setRequestMethod(method.toUpperCase());
            connection.setConnectTimeout(TIMEOUT_LENGTH);
            connection.setReadTimeout(TIMEOUT_LENGTH);
            connection.setInstanceFollowRedirects(true);

            connection.setDoInput(true);
            if (!method.equals("GET"))
            {
                connection.setDoOutput(true);
            }

            if (headers != null)
            {
                headers.forEach(connection::setRequestProperty);
            }

            if (body != null && !body.isEmpty())
            {
                writeTo(connection.getOutputStream(), body);
            }

            connection.connect();

            final int statusCode = connection.getResponseCode();
            if (runnable != null)
            {
                // 204 No Content
                if (statusCode == 204)
                {
                    runnable.accept(null);
                    return 204;
                }

                if (statusCode == 200)
                {
                    runnable.accept(connection.getInputStream());
                } else if (statusCode == 418)
                {
                    // this means do nothing lol
                    LOGGER.debug("I'm a teapot!");
                } else
                {
                    runnable.accept(connection.getErrorStream());
                }
            }

            connection.disconnect();
            return statusCode;
        } catch (final IOException e)
        {
            LOGGER.error("Failed to make connection", e);
        }
        return -1;
    }

    public static Consumer<InputStream> downloadFile(final File file)
    {
        return (is) ->
        {
            try (final OutputStream os = Files.newOutputStream(file.toPath()))
            {
                if (os == null)
                {
                    throw new RuntimeException("No OutputStream created for file");
                }
                int b;
                while ((b = is.read()) != -1)
                {
                    os.write(b);
                }
                is.close();
            } catch (final IOException e)
            {
                LOGGER.error("Failed to transfer server data to file", e);
            }
        };
    }

    private static String fixEndpoint(String endpoint)
    {
        if (endpoint.startsWith("/"))
        {
            endpoint = endpoint.substring(1);
        }
        return endpoint;
    }

    public static void writeTo(final OutputStream os, final String data)
    {
        try
        {
            final byte[] bytes = data.getBytes(StandardCharsets.UTF_8);
            os.write(bytes, 0, bytes.length);
            os.close();
        } catch (final IOException e)
        {
            LOGGER.error("Failed to write to server data stream", e);
        }
    }

    public static String readFrom(final InputStream is)
    {
        try
        {
            final StringBuilder builder = new StringBuilder();
            int b;
            while ((b = is.read()) != -1)
            {
                builder.append((char) b);
            }
            is.close();
            return builder.toString();
        } catch (final IOException e)
        {
            LOGGER.error("Failed to read from server data stream", e);
        }
        return null;
    }

    private HTTP()
    {
        // no-op
    }
}
