/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.server.server.endpoint.impl;

import io.fusionauth.http.server.HTTPRequest;
import io.fusionauth.http.server.HTTPResponse;
import us.nebula.server.BuildConfig;
import us.nebula.server.server.endpoint.IEndpoint;

import java.io.IOException;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class BaseEndpoint implements IEndpoint
{
    @Override
    public void handle(final HTTPRequest req, final HTTPResponse res) throws IOException
    {
        writeResponse(res, BuildConfig.VERSION, 200);
    }

    @Override
    public String getEndpoint()
    {
        return "/";
    }
}
