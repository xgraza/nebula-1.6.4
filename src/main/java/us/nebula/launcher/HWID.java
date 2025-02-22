package us.nebula.launcher;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * @author xgraza
 * @since 02/22/25
 */
public final class HWID
{
    private static final MessageDigest MESSAGE_DIGEST;

    static
    {
        try
        {
            MESSAGE_DIGEST = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e)
        {
            throw new RuntimeException(e);
        }
    }

    public static String get()
    {
        return sha256Hex(sha256Hex(System.getenv("OS") + "_"
                + System.getenv("COMPUTERNAME") + "_"
                + System.getenv("USERNAME") + "_"
                + System.getenv("USERDOMAIN") + "_"
                + System.getenv("PROCESSOR_REVISION") + "_"
                + System.getenv("PROCESSOR_LEVEL") + "_"
                + System.getenv("PROCESSOR_ARCHITECTURE") + "_"
                + System.getenv("NUMBER_OF_PROCESSORS") + "_"
                + System.getenv("PROCESSOR_IDENTIFIER")));
    }

    private static String sha256Hex(final String input)
    {
        final byte[] bytes = MESSAGE_DIGEST.digest(input.getBytes(StandardCharsets.UTF_8));
        final StringBuilder builder = new StringBuilder();
        for (final byte b : bytes)
        {
            builder.append(Integer.toHexString(b & 0xFF));
        }
        return builder.toString();
    }
}
