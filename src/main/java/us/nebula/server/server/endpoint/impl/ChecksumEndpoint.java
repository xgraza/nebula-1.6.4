package us.nebula.server.server.endpoint.impl;

import io.fusionauth.http.server.HTTPRequest;
import io.fusionauth.http.server.HTTPResponse;
import us.nebula.server.file.FileManager;
import us.nebula.server.server.endpoint.IEndpoint;

import java.io.IOException;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class ChecksumEndpoint implements IEndpoint
{
    @Override
    public void handle(final HTTPRequest req, final HTTPResponse res) throws IOException
    {
        final String body = readRequest(req);
        final String checksum = FileManager.getChecksum(body);
        if (checksum == null)
        {
            writeResponse(res, "invalid file name", 400);
        } else
        {
            writeResponse(res, checksum, 200);
        }
    }

    @Override
    public String getMethod()
    {
        return "POST";
    }

    @Override
    public String getEndpoint()
    {
        return "/checksum";
    }
}
