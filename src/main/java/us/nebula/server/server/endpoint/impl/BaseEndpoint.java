/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.server.server.endpoint.impl;

import us.nebula.server.BuildConfig;
import us.nebula.server.server.endpoint.Endpoint;

import java.io.IOException;

/**
 * @author xgraza
 * @since 1.0.0
 */
@Endpoint.Metadata("/")
public final class BaseEndpoint extends Endpoint
{
    @Override
    public void handle() throws IOException
    {
        writeResponse(BuildConfig.VERSION, OK);
    }
}
