/*
 * Copyright (c) xgraza 2025
 */

package us.nebula.server.server;

import io.fusionauth.http.server.*;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import us.nebula.server.ServerProperties;
import us.nebula.server.server.endpoint.Endpoint;
import us.nebula.server.server.endpoint.impl.*;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;

/**
 * @author xgraza
 * @since 1.0.0
 */
public final class NebulaHTTPServer implements HTTPHandler
{
    private static final Logger LOGGER = LogManager.getLogger("Nebula HTTP");
    private static final int PORT = Integer.parseInt(ServerProperties.get("server_port"));

    private final Map<String, Endpoint> endpointMap = new LinkedHashMap<>();
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
        registerEndpoint(new UploadEndpoint());         // /upload

        LOGGER.info("Registered {} endpoints", endpointMap.size());
    }

    @Override
    public void handle(final HTTPRequest req, final HTTPResponse res) throws IOException
    {
        final String path = req.getPath();
        final Endpoint endpoint = endpointMap.get(req.getPath());
        if (endpoint != null)
        {
            LOGGER.info("Request on {}:{} from {}",
                    req.getMethod(), path, req.getIPAddress());

            final String method = endpoint.getMetadata().method();
            if (!req.getMethod().name().equals(method))
            {
                endpoint.writeResponse(
                        "request must be sent with " + method, 400);
                return;
            }

            final List<String> missingHeaders = getMisingHeaders(req, endpoint);
            if (!missingHeaders.isEmpty())
            {
                endpoint.writeResponse(
                        "missing headers: " + String.join(", ", missingHeaders),
                        400);
                return;
            }

            try
            {
                endpoint.setRequestAndResponse(req, res);
                endpoint.handle();
            } catch (IOException e)
            {
                LOGGER.error("Failed to handle endpoint", e);
            }
            return;
        }
        send404(res);
    }

    private List<String> getMisingHeaders(final HTTPRequest req, final Endpoint endpoint)
    {
        final String[] requiredHeaders = endpoint.getMetadata().headers();
        if (requiredHeaders == null || requiredHeaders.length == 0)
        {
            return Collections.emptyList();
        }
        final List<String> missingHeaders = new LinkedList<>();
        for (final String name : requiredHeaders)
        {
            final String value = req.getHeader(name);
            endpoint.setHeader(name, value);
            if (value == null)
            {
                missingHeaders.add(name);
            }
        }
        return missingHeaders;
    }

    private void send404(final HTTPResponse res) throws IOException
    {
        res.setStatus(Endpoint.INVALID_RESOURCE);
        final byte[] messageBytes = "Not found".getBytes(StandardCharsets.UTF_8);
        final OutputStream os = res.getOutputStream();
        os.write(messageBytes, 0, messageBytes.length);
        os.close();
    }

    private void registerEndpoint(final Endpoint endpoint)
    {
        endpointMap.put(endpoint.getMetadata().value(), endpoint);
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
