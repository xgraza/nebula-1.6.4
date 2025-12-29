/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.server.server.endpoint.impl;

import us.nebula.server.file.FileManager;
import us.nebula.server.server.endpoint.Endpoint;

import java.io.IOException;

/**
 * @author xgraza
 * @since 1.0.0
 */
@Endpoint.Metadata("/favicon.ico")
public final class FaviconEndpoint extends Endpoint
{
    @Override
    public void handle() throws IOException
    {
        writeFile(FileManager.getFile("16x.png"));
    }
}
