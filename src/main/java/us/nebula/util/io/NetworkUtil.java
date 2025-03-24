package us.nebula.util.io;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.stream.Collectors;

/**
 * @author xgraza
 * @since 03/24/25
 */
public final class NetworkUtil
{
    public static String queryString(final String[]... params)
    {
        return Arrays.stream(params).map((arr) ->
        {
            String value = "";
            if (arr.length == 2)
            {
                value = "=" + arr[1];
            }
            return encodeUri(arr[0]) + value;
        }).collect(Collectors.joining("&"));
    }

    public static String encodeUri(final String t)
    {
        try
        {
            return URLEncoder.encode(t, StandardCharsets.UTF_8.toString());
        } catch (final UnsupportedEncodingException e)
        {
            return t;
        }
    }
}
