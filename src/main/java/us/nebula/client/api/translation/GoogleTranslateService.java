package us.nebula.client.api.translation;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonPrimitive;
import us.nebula.client.Nebula;
import us.nebula.client.util.io.FileUtil;
import us.nebula.client.util.io.NetworkUtil;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * @author xgraza
 * @since 03/24/25
 */
public final class GoogleTranslateService
{
    private static final String GOOGLE_TRANSLATE_API
            = "https://translate.googleapis.com/translate_a/single";

    public static GoogleTranslateService INSTANCE = new GoogleTranslateService();

    private GoogleTranslateService()
    {

    }

    public void translate(final Language target,
                          final Language source,
                          final String text,
                          final TranslationResponse translationResponse)
    {
        if (source == Language.AUTO && target == Language.AUTO)
        {
            throw new RuntimeException("src & target cannot both be AUTO");
        }
        Nebula.INSTANCE.getExecutor().execute(() ->
        {
            try
            {
                makeRequest(target, source, text, translationResponse);
            } catch (final IOException e)
            {
                translationResponse.provideResult(null, null);
                throw new RuntimeException(e);
            }
        });
    }

    private void makeRequest(final Language target,
                             final Language source,
                             final String text,
                             final TranslationResponse translationResponse)
            throws IOException
    {
        final String URL = GOOGLE_TRANSLATE_API + "?"
                + NetworkUtil.queryString(new String[][]{
                { "client", "gtx" },
                { "dt", "at" },
                { "dt", "bd" },
                { "dt", "ex" },
                { "dt", "ld" },
                { "dt", "md" },
                { "dt", "qca" },
                { "dt", "rw" },
                { "dt", "rm" },
                { "dt", "ss" },
                { "dt", "t" },
                { "ie", "UTF-8" },
                { "oe", "UTF-8" },
                { "otf", "1" },
                { "ssel", "0" },
                { "tsel", "0" },
                { "tk", "ilovebushbus" },
                { "sl", source.getLocale() },
                { "tl", target.getLocale() },
                { "hl", target.getLocale() },
                { "q", NetworkUtil.encodeUri(text) }
        });
        final HttpURLConnection connection = (HttpURLConnection) new URL(URL).openConnection();
        connection.setConnectTimeout(5000);
        connection.setReadTimeout(5000);
        connection.setDoOutput(true);
        connection.setUseCaches(false);

        connection.connect();

        if (connection.getResponseCode() != 200)
        {
            return;
        }

        String output;
        try (final InputStream is = connection.getInputStream())
        {
            final StringBuilder builder = new StringBuilder();
            int b;
            while ((b = is.read()) != -1)
            {
                builder.append((char) b);
            }
            output = builder.toString();
        }

        provideResponse(FileUtil.JSON_PARSER.parse(output), translationResponse);
    }

    private void provideResponse(final JsonElement e, final TranslationResponse response)
    {
        if (!e.isJsonArray())
        {
            throw new RuntimeException("expected JsonArray, got " + e);
        }
        final JsonArray obj = e.getAsJsonArray();

        final Language source = Language.fromLocale(obj.get(2).getAsString());

        final StringBuilder builder = new StringBuilder();
        final JsonArray arr = obj.get(0).getAsJsonArray();
        for (JsonElement element : arr)
        {
            if (element.isJsonArray())
            {
                JsonArray a = element.getAsJsonArray();
                JsonElement ee = a.get(0);
                if (ee.isJsonPrimitive())
                {
                    JsonPrimitive primitive = ee.getAsJsonPrimitive();
                    if (primitive.isString())
                    {
                        builder.append(primitive.getAsString().replaceAll("\n", "")).append(" ");
                    }
                }
            }
        }

        response.provideResult(source, builder.toString());
    }
}
