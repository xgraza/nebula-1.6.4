/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.server.server.endpoint.impl;

import io.fusionauth.http.server.HTTPRequest;
import io.fusionauth.http.server.HTTPResponse;
import us.nebula.server.file.FileManager;
import us.nebula.server.server.endpoint.IEndpoint;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class FaviconEndpoint implements IEndpoint
{
    private byte[] bytes = null;

    @Override
    public void handle(final HTTPRequest req, final HTTPResponse res) throws IOException
    {
        if (bytes == null)
        {
            cacheBytes();
        }
        writeResponse(res, bytes, 200);
    }

    private void cacheBytes() throws IOException
    {
        final File faviconFile = FileManager.getFile("16x.png");
        try (final InputStream is = Files.newInputStream(faviconFile.toPath()))
        {
            bytes = is.readAllBytes();
        }
    }

    @Override
    public String getEndpoint()
    {
        return "/favicon.ico";
    }

    @Override
    public boolean logWhenAccessed()
    {
        return false;
    }
}
