/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.server.server;

import io.fusionauth.http.server.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.nebula.server.ServerProperties;
import us.nebula.server.server.endpoint.IEndpoint;
import us.nebula.server.server.endpoint.impl.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class NebulaHTTPServer implements HTTPHandler
{
    private static final Logger LOGGER = LogManager.getLogger("Nebula HTTP");
    private static final int PORT = Integer.parseInt(ServerProperties.get("server_port"));

    private final Map<String, IEndpoint> endpointMap = new LinkedHashMap<>();
    private final HTTPServer server;

    private NebulaHTTPServer()
    {
        server = new HTTPServer()
                .withConfiguration(new HTTPServerConfiguration()
                        .withHandler(this)
                        .withLoggerFactory(new HTTPServerLogger(LOGGER))
                        .withUnexpectedExceptionHandler(
                                (ctx) ->
                                        LOGGER.error("HTTP server exception",
                                                ctx.getThrowable())))
                .withListener(getConfiguration());

        registerEndpoint(new BaseEndpoint());           // /
        registerEndpoint(new FaviconEndpoint());        // /favicon.ico
        registerEndpoint(new ClientManifestEndpoint()); // /manifest
        registerEndpoint(new DownloadEndpoint());       // /download
        registerEndpoint(new ChecksumEndpoint());       // /checksum

        LOGGER.info("Registered {} endpoints", endpointMap.size());
    }

    @Override
    public void handle(final HTTPRequest req, final HTTPResponse res) throws IOException
    {
        final String path = req.getPath();
        final IEndpoint endpoint = endpointMap.get(req.getPath());
        if (endpoint == null)
        {
            send404(res);
            return;
        }
        if (endpoint.logWhenAccessed())
        {
            LOGGER.info("Request on {}:{} from {}", req.getMethod(), path, req.getIPAddress());
        }
        if (!req.getMethod().name().equals(endpoint.getMethod()))
        {
            endpoint.writeResponse(res,
                    "request must be sent with " + endpoint.getMethod(),
                    400);
            return;
        }
        try
        {
            endpoint.handle(req, res);
        } catch (IOException e)
        {
            LOGGER.error("Failed to handle endpoint", e);
        }
    }

    private void send404(final HTTPResponse res) throws IOException
    {
        res.setStatus(404);
        final byte[] messageBytes = "Not found".getBytes(StandardCharsets.UTF_8);
        final OutputStream os = res.getOutputStream();
        os.write(messageBytes, 0, messageBytes.length);
        os.close();
    }

    private void registerEndpoint(final IEndpoint endpoint)
    {
        endpointMap.put(endpoint.getEndpoint(), endpoint);
    }

    private HTTPListenerConfiguration getConfiguration()
    {
        // TODO: SSL
        return new HTTPListenerConfiguration(PORT);
    }

    public void start()
    {
        server.start();
    }

    public static void init()
    {
        final NebulaHTTPServer server = new NebulaHTTPServer();
        server.start();
    }
}
