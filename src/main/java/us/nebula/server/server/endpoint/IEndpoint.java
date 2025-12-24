/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.server.server.endpoint;

import io.fusionauth.http.server.HTTPRequest;
import io.fusionauth.http.server.HTTPResponse;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

/**
 * @author xgraza
 * @since 1.0.0
 */
public interface IEndpoint
{
    void handle(final HTTPRequest req, final HTTPResponse res) throws IOException;

    default String readRequest(final HTTPRequest req) throws IOException
    {
        final InputStream is = req.getInputStream();
        final StringBuilder builder = new StringBuilder();
        {
            int b;
            while ((b = is.read()) != -1)
            {
                builder.append((char) b);
            }
        }
        is.close();
        return builder.toString();
    }

    default void writeResponse(final HTTPResponse res,
                               final String message,
                               final int statusCode) throws IOException
    {
        writeResponse(res, message.getBytes(StandardCharsets.UTF_8), statusCode);
    }

    default void writeResponse(final HTTPResponse res,
                               final byte[] bytes,
                               final int statusCode) throws IOException
    {
        res.setStatus(statusCode);
        res.getOutputStream().write(bytes, 0, bytes.length);
        res.getOutputStream().close();
    }

    default void writeFile(final HTTPResponse res, final File file) throws IOException
    {
        // determine content-type
        if (file.getName().contains("."))
        {
            final String[] parts = file.getName().split("\\.");
            final String ext = parts[parts.length - 1].toLowerCase();
            res.setContentType(switch (ext)
            {
                default -> "application/octet-stream";
            });
            res.setContentLength(file.length());
            res.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\";");
        }

        res.setStatus(200);
        final OutputStream os = res.getOutputStream();
        try (final InputStream is = Files.newInputStream(file.toPath()))
        {
            int b;
            while ((b = is.read()) != -1)
            {
                os.write(b);
            }
            os.close();
        }
    }

    String getEndpoint();

    default String getMethod()
    {
        return "GET";
    }

    default String[] getRequiredHeaders()
    {
        return new String[0];
    }

    default boolean logWhenAccessed()
    {
        return true;
    }
}
