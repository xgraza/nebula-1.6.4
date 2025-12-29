package us.nebula.server.server.endpoint;

import io.fusionauth.http.server.HTTPRequest;
import io.fusionauth.http.server.HTTPResponse;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * @author xgraza
 * @since 1.0.0
 */
public abstract class Endpoint
{
    public static final int OK = 200;
    public static final int BAD_REQUEST = 400;
    public static final int UNAUTHENTICATED = 403;
    public static final int INVALID_RESOURCE = 404;

    private final Map<String, String> requiredHeadersMap = new LinkedHashMap<>();
    private final Metadata metadata;
    protected final Logger logger;

    protected HTTPResponse response;
    protected HTTPRequest request;

    public Endpoint()
    {
        metadata = getClass().getDeclaredAnnotation(Metadata.class);
        if (metadata == null)
        {
            throw new RuntimeException("@Endpoint.Metadata needs to be present");
        }
        logger = LogManager.getLogger(metadata.value());
    }

    public abstract void handle() throws IOException;

    public String readBody() throws IOException
    {
        final InputStream is = request.getInputStream();
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

    public void writeResponse(final String message, final int statusCode) throws IOException
    {
        writeResponse(message.getBytes(StandardCharsets.UTF_8), statusCode);
    }

    public void writeResponse(final byte[] bytes, final int statusCode) throws IOException
    {
        response.setStatus(statusCode);
        response.getOutputStream().write(bytes, 0, bytes.length);
        response.getOutputStream().close();
    }

    public void writeFile(final File file) throws IOException
    {
        // determine content-type
        if (file.getName().contains("."))
        {
            final String[] parts = file.getName().split("\\.");
            final String ext = parts[parts.length - 1].toLowerCase();
            response.setContentType(switch (ext)
            {
                default -> "application/octet-stream";
            });
            response.setContentLength(file.length());
            response.setHeader("Content-Disposition", "attachment; filename=\"" + file.getName() + "\";");
        }

        response.setStatus(200);
        final OutputStream os = response.getOutputStream();
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

    public void setRequestAndResponse(final HTTPRequest request,
                                      final HTTPResponse response)
    {
        this.request = request;
        this.response = response;
    }

    public Metadata getMetadata()
    {
        return metadata;
    }

    public String getRequiredHeader(final String name)
    {
        return requiredHeadersMap.get(name);
    }

    public void setHeader(final String name, final String value)
    {
        requiredHeadersMap.put(name, value);
    }

    @Retention(RetentionPolicy.RUNTIME)
    public @interface Metadata
    {
        String value();

        String method() default "GET";

        String[] headers() default {};
    }
}
