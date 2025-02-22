package us.nebula.launcher;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;

/**
 * @author xgraza
 * @since 02/22/25
 */
public final class Connection
{
    private static final String BASE_URL = "http://localhost:8080/api/";

    private final Map<String, String> headers = new HashMap<>();
    private final URL url;

    public Connection(final String path)
    {
        try
        {
            url = new URL(BASE_URL + path);
        } catch (final MalformedURLException e)
        {
            throw new RuntimeException(e);
        }
    }

    public Connection addHeader(String k, final String v)
    {
        k = k.replaceAll("\\s", "-");
        headers.put(k, v);
        return this;
    }

    public void connect(final Consumer<InputStream> callback)
    {
        HttpURLConnection connection = null;
        try
        {
            connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(5_000);
            connection.setReadTimeout(5_000);

            if (!headers.isEmpty())
            {
                headers.forEach(connection::setRequestProperty);
            }

            connection.connect();
            if (connection.getResponseCode() != 200)
            {
                callback.accept(null);
                connection.disconnect();
                return;
            }

            callback.accept(connection.getInputStream());
        } catch (final IOException e)
        {
            throw new RuntimeException(e);
        } finally
        {
            if (connection != null)
            {
                connection.disconnect();
            }
        }
    }
}
