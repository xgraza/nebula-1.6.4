package us.nebula.launcher;

import java.nio.charset.StandardCharsets;

/**
 * @author xgraza
 * @since 02/22/25
 */
public final class Util
{
    public static String bytesToHex(final String s)
    {
        return bytesToHex(s.getBytes(StandardCharsets.UTF_8));
    }

    public static String bytesToHex(final byte[] bytes)
    {
        final StringBuilder builder = new StringBuilder();
        for (final byte b : bytes) {
            builder.append(String.format("%02x", b));
        }
        return builder.toString();
    }
}
